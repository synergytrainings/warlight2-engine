// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.engine.io;

import org.apache.commons.io.output.TeeOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IOPlayer class
 * 
 * Does the communication between the bot process and the engine
 * 
 * @author Jackie Xu <jackie@starapple.nl>, Jim van Eeden <jim@starapple.nl>
 */
public class IOPlayer implements Runnable {
    
    private Process process;
    private OutputStreamWriter inputStream;
    private InputStreamGobbler outputGobbler;
    private InputStreamGobbler errorGobbler;
    private StringBuilder dump;
    private int errorCounter;
    private boolean finished;
    private final int maxErrors = 2;
    private int playerIndex;
    public volatile String response;
    
    public IOPlayer(Process process, int index) {

        if(index==0){
            this.inputStream = new OutputStreamWriter(process.getOutputStream());
        }
        else {
            this.inputStream = new OutputStreamWriter(new TeeOutputStream(process.getOutputStream(), System.out));
        }
    	this.outputGobbler = new InputStreamGobbler(process.getInputStream(), this, "output", index==1);
    	this.errorGobbler = new InputStreamGobbler(process.getErrorStream(), this, "error", index==1);
        this.process = process;
        this.dump = new StringBuilder();
        this.errorCounter = 0;
        this.finished = false;
        this.playerIndex = index;
    }
    
    // processes a line by reading it or writing it
    public void process(String line, String type) throws IOException {
        if (!this.finished) {
        	switch (type) {
        	case "input":
                try {
            		this.inputStream.write(line + "\n");
            		this.inputStream.flush();
                } catch(IOException e) {
                    System.err.println("Writing to bot failed:");
                    System.err.println("    " + line);
                }
                addToDump(line + "\n");
        		break;
        	case "output":
//        		System.out.println("out: " + line);
        		break;
        	case "error":
//        		System.out.println("error: " + line);
        		break;
        	}
        }
    }
    
    // waits for a response from the bot
    public String getResponse(long timeOut) {
    	long timeStart = System.currentTimeMillis();
    	String response;
		
    	if (this.errorCounter > this.maxErrors) {
    		addToDump("Maximum number (" + this.maxErrors + ") of time-outs reached: skipping all moves.\n");
    		return "";
    	}
    	
    	while(this.response == null) {
    		long timeNow = System.currentTimeMillis();
			long timeElapsed = timeNow - timeStart;
			
			if(timeElapsed >= timeOut) {
				addToDump("Response timed out (" + timeOut + "ms), let your bot return 'No moves' instead of nothing or make it faster.\n");
				System.err.print(this.errorGobbler.getData());
				this.errorCounter++;
                if (this.errorCounter > this.maxErrors) {
                    finish();
                }
                addToDump("Output from your bot: null");
				return "";
			}
			
			try { Thread.sleep(2); } catch (InterruptedException e) {}
    	}
		if(this.response.equalsIgnoreCase("No moves")) {
			this.response = null;
            addToDump("Output from your bot: \"No moves\"\n");
			return "";
		}
		
		response = this.response;
		this.response = null;

		addToDump("Output from your bot: \"" + response + "\"\n");
		return response;
    }
    
    // ends the bot process and it's communication
    public void finish() {

        if(this.finished)
            return;

    	try {
            this.inputStream.close();
        } catch (IOException e) {}

    	this.process.destroy();
    	try {
    		this.process.waitFor();
    	} catch (InterruptedException ex) {
    		Logger.getLogger(IOPlayer.class.getName()).log(Level.SEVERE, null, ex);
    	}

        this.finished = true;
    }
    
    public Process getProcess() {
        return this.process;
    }
    
    public void addToDump(String dumpy){
		dump.append(dumpy);
	}
    
    public String getStdout() {
    	return this.outputGobbler.getData();
    }
    
    public String getStderr() {
    	return this.errorGobbler.getData();
    }
    
    public String getDump() {
    	return dump.toString();
    }

    @Override
    // start communication with the bot
    public void run() {
        this.outputGobbler.start();
        this.errorGobbler.start();
    }
}
