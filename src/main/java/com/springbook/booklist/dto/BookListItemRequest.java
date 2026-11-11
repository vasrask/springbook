package com.springbook.booklist.dto;

import jakarta.validation.constraints.NotNull;

public record BookListItemRequest(

        @NotNull
        Long userBookId

) {}
