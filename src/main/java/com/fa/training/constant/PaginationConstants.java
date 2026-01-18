package com.fa.training.constant;

/**
 * Pagination and sorting default values
 */
public final class PaginationConstants {

    private PaginationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    public static final String SORT_DIRECTION_DESC = "desc";
}
