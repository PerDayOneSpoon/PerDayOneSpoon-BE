package com.sparta.perdayonespoon.spring_batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

public class CustomReader<T> implements ItemReader {

    private final List<T> items;

    public CustomReader(List<T> items) {
        this.items = items;
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if(!items.isEmpty()) {
            return items.remove(0);
        }
        return null;        // chunk 반복의 끝은 null 을 return 할 때 라서.
    }
}
