package com.marlodev.app_android.data.network.model.tag;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {

    private Integer id;
    private String name;

    /**
     * Constructor de conveniencia para crear una petición solo con el nombre.
     * @param name El nombre del tag a crear/actualizar.
     */
    public TagRequest(String name) {
        this.name = name;
    }

    /**
     * Constructor de conveniencia para crear una petición solo con el ID.
     * @param id El ID del tag a asociar.
     */
    public TagRequest(Integer id) {
        this.id = id;
    }
}
