/**
 * Warlight AI Game Bot
 * <p>
 * Last update: January 29, 2015
 *
 * @author Jim van Eeden
 * @version 1.1
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package map;

import java.util.LinkedList;


public class Region {

    private int id;
    private LinkedList<Region> neighbors;
    private SuperRegion superRegion;
    private int armies;
    private String playerName;
    private boolean border;
    private int layerNumber;

    public Region(int id, SuperRegion superRegion) {
        this.id = id;
        this.superRegion = superRegion;
        this.neighbors = new LinkedList<Region>();
        this.playerName = "unknown";
        this.armies = 0;

        superRegion.addSubRegion(this);
    }

    public Region(int id, SuperRegion superRegion, String playerName, int armies) {
        this.id = id;
        this.superRegion = superRegion;
        this.neighbors = new LinkedList<Region>();
        this.playerName = playerName;
        this.armies = armies;

        superRegion.addSubRegion(this);
    }

    public void addNeighbor(Region neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
            neighbor.addNeighbor(this);
        }
    }

    /**
     * @param region a Region object
     * @return True if this Region is a neighbor of given Region, false otherwise
     */
    public boolean isNeighbor(Region region) {
        if (neighbors.contains(region))
            return true;
        return false;
    }

    /**
     * @param playerName A string with a player's name
     * @return True if this region is owned by given playerName, false otherwise
     */
    public boolean ownedByPlayer(String playerName) {
        if (playerName.equals(this.playerName))
            return true;
        return false;
    }

    /**
     * @param armies Sets the number of armies that are on this Region
     */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * @param playerName Sets the Name of the player that this Region belongs to
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return The id of this Region
     */
    public int getId() {
        return id;
    }

    /**
     * @return A list of this Region's neighboring Regions
     */
    public LinkedList<Region> getNeighbors() {
        return neighbors;
    }

    /**
     * @return The SuperRegion this Region is part of
     */
    public SuperRegion getSuperRegion() {
        return superRegion;
    }

    /**
     * @return The number of armies on this region
     */
    public int getArmies() {
        return armies;
    }

    /**
     * @return A string with the name of the player that owns this region
     */
    public String getPlayerName() {
        return playerName;
    }

    public boolean isBorder() {
        for (Region region : neighbors) {
            if (!playerName.equals(region.getPlayerName())) {
//                System.err.println("Region "+getId()+" is border");
                return true;
            }
        }
//        System.err.println("Region "+getId()+" is not border");
        return false;
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    public int getLayerNumber() {
        return layerNumber;
    }

    public boolean isBorderTo(String playerName) {
        for (Region region : neighbors) {
            if (playerName.equals(region.getPlayerName())) {
                return true;
            }
        }
        return false;
    }

    public int countByOwner(String playerName) {
        int count = 0;

        for (Region region : neighbors) {
            if (region.getPlayerName().equals(playerName)) {
                count++;
            }
        }

        return count;
    }
}
