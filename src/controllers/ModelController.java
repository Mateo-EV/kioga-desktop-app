package controllers;

import utils.ApiClient;

public interface ModelController<E> {
    void findById(int id, ApiClient.onResponse onResponse);
    void findAll(ApiClient.onResponse onResponse);
    void save(E entity, ApiClient.onResponse onResponse);
    void update(E entity, ApiClient.onResponse onResponse);
    void update(E entity, String field, ApiClient.onResponse onResponse);
    void delete(int id, ApiClient.onResponse onResponse);
}
