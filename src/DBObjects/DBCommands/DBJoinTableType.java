package DBObjects.DBCommands;

/**
 * PRIMARY : The table that is joining on
 * SECONDARY : The table that is joining by
 *
 * Primary join secondary on x = y
 */
public enum DBJoinTableType {
    PRIMARY, SECONDARY;
}
