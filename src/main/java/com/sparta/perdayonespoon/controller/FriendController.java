package com.sparta.perdayonespoon.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="친구 페이지 REST API")
@RequestMapping("/api/friend/")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class FriendController {
}
