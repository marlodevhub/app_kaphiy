package com.marlodev.app_android.domain.repository;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Tag;

import java.util.List;

//✔️ Se deine que se va hacer pero no dices cómo se hace,
//   no importa si los datos vienen de API, Base de datos, cache, o archivos.
//✔️ Aquí no usa nada de retrofit, dtos, mappers ni websocket, etc.
public interface TagRepository {
    LiveData<List<Tag>> loadTags();
    LiveData<Tag> getTagById(Integer id);
    void createTag(String name);
    void updateTag(Integer id, String newName);
    void deleteTag(Integer id);
}
