package com.sparta.perdayonespoon.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QGoal is a Querydsl query type for Goal
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QGoal extends EntityPathBase<Goal> {

    private static final long serialVersionUID = -2020094113L;

    public static final QGoal goal = new QGoal("goal");

    public final BooleanPath achievementCheck = createBoolean("achievementCheck");

    public final NumberPath<Integer> category = createNumber("category", Integer.class);

    public final NumberPath<Integer> characterId = createNumber("characterId", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> currentDate = createDateTime("currentDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath privateCheck = createBoolean("privateCheck");

    public final StringPath socialId = createString("socialId");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath time = createString("time");

    public final StringPath title = createString("title");

    public QGoal(String variable) {
        super(Goal.class, forVariable(variable));
    }

    public QGoal(Path<? extends Goal> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGoal(PathMetadata metadata) {
        super(Goal.class, metadata);
    }

}

