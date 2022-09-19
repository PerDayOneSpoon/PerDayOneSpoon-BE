package com.sparta.perdayonespoon.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeletedUrlPath is a Querydsl query type for DeletedUrlPath
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDeletedUrlPath extends EntityPathBase<DeletedUrlPath> {

    private static final long serialVersionUID = -852002521L;

    public static final QDeletedUrlPath deletedUrlPath1 = new QDeletedUrlPath("deletedUrlPath1");

    public final StringPath deletedUrlPath = createString("deletedUrlPath");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDeletedUrlPath(String variable) {
        super(DeletedUrlPath.class, forVariable(variable));
    }

    public QDeletedUrlPath(Path<? extends DeletedUrlPath> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeletedUrlPath(PathMetadata metadata) {
        super(DeletedUrlPath.class, metadata);
    }

}

