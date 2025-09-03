package com.ogd.stockdiary.presentation.controller;

import com.ogd.stockdiary.proto.UserProto.UserRequest;
import com.ogd.stockdiary.proto.UserProto.UserResponse;
import com.ogd.stockdiary.proto.UserProto.UserListRequest;
import com.ogd.stockdiary.proto.UserProto.UserListResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/protobuf/users")
public class UserProtobufController {

    @PostMapping(
            consumes = {"application/x-protobuf", "application/json"},
            produces = {"application/x-protobuf", "application/json"}
    )
    public UserResponse createUser(@RequestBody UserRequest request) {
        return UserResponse.newBuilder()
                .setUserId("user_" + System.currentTimeMillis())
                .setName(request.getName())
                .setEmail(request.getEmail())
                .setCreatedAt(Instant.now().toEpochMilli())
                .setIsActive(true)
                .build();
    }

    @GetMapping(
            value = "/{userId}",
            produces = {"application/x-protobuf", "application/json"}
    )
    public UserResponse getUser(@PathVariable String userId) {
        return UserResponse.newBuilder()
                .setUserId(userId)
                .setName("Sample User")
                .setEmail("sample@example.com")
                .setCreatedAt(Instant.now().toEpochMilli())
                .setIsActive(true)
                .build();
    }

    @PostMapping(
            value = "/list",
            consumes = {"application/x-protobuf", "application/json"},
            produces = {"application/x-protobuf", "application/json"}
    )
    public UserListResponse getUserList(@RequestBody UserListRequest request) {
        UserResponse user1 = UserResponse.newBuilder()
                .setUserId("user_1")
                .setName("User One")
                .setEmail("user1@example.com")
                .setCreatedAt(Instant.now().toEpochMilli())
                .setIsActive(true)
                .build();

        UserResponse user2 = UserResponse.newBuilder()
                .setUserId("user_2")
                .setName("User Two")
                .setEmail("user2@example.com")
                .setCreatedAt(Instant.now().toEpochMilli())
                .setIsActive(true)
                .build();

        return UserListResponse.newBuilder()
                .addUsers(user1)
                .addUsers(user2)
                .setTotalCount(2)
                .setCurrentPage(request.getPage())
                .build();
    }
}