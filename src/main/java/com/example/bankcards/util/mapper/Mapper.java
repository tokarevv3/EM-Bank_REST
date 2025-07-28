package com.example.bankcards.util.mapper;

public interface Mapper<F, T> {

    T map(F obj);

    default T map(F fromObj, T toObj) {
        return toObj;
    }
}
