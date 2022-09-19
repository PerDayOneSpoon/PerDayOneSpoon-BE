package com.sparta.perdayonespoon.util;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTimestamped is a Querydsl query type for Timestamped
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QTimestamped extends EntityPathBase<Timestamped> {

    private static final long serialVersionUID = 380665895L;

    public static final QTimestamped timestamped = new QTimestamped("timestamped");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> modifiedAt = createDate("modifiedAt", java.time.LocalDate.class);

    public QTimestamped(String variable) {
        super(Timestamped.class, forVariable(variable));
    }

    public QTimestamped(Path<? extends Timestamped> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTimestamped(PathMetadata metadata) {
        super(Timestamped.class, metadata);
    }

}

