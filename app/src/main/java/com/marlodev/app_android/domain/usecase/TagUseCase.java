package com.marlodev.app_android.domain.usecase;

import com.marlodev.app_android.domain.repository.TagRepository;

//✔️ Depende solo de la interfaz TagRepository, no de la implementación TagRepositoryImpl.
//  Aquí Valída datos.
//  Ejecuta una acción.
// Orquesta lógica de Negocio.
// (esto está más del lado de la ui que la del repositorioImpl)
//  se usan principalmente para acciones de negocio que requieren reglas o validaciones
//  como crear, actualizar o eliminar un tag.
public class TagUseCase {
    private final TagRepository repository;

    public TagUseCase(TagRepository repository) {
        this.repository = repository;
    }

    public void execute(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        // ------------------------------
        // Normalización / validación
        // ------------------------------
        String normalizedName = name.trim();               // Elimina espacios al inicio y fin
        if (normalizedName.length() > 50) {                // Limitar longitud
            throw new IllegalArgumentException("El nombre es demasiado largo");
        }
        normalizedName = normalizedName.toLowerCase();    // Pasa a minúsculas para consistencia
        if (!normalizedName.matches("[a-z0-9\\s]+")) {    // Solo letras, números y espacios
            throw new IllegalArgumentException("El nombre contiene caracteres inválidos");
        }

        // ------------------------------
        // Ejecuta acción en repositorio
        // ------------------------------
        repository.createTag(normalizedName);
    }

}
