module Capture {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    requires firebase.admin;
    requires com.fasterxml.jackson.core;
    requires com.google.gson;
    requires slf4j.simple;
    requires com.google.auth.oauth2;
    requires com.google.auth;

    requires reactfx;
    requires MaterialFX;

    opens application;
    opens application.controller;
}