package de.learnlib.alex.utils;

import java.util.LinkedList;

/**
 * Helper class to allow batch paths to have a csv-list of IDs.
 */
public class IdsList extends LinkedList<Long> {

    /**
     * Constructor
     * This is neede for Jersey, so that an IdList can be used as PathParameter.
     *
     * @param value
     *         The ids as comma separated list.
     */
    public IdsList(String value) {
        String[] parts = value.split(",");
        if (parts.length == 0) {
            throw new IllegalArgumentException("No IDs found!");
        }

        for (String number : parts) {
            add(Long.valueOf(number));
        }
    }
}