package es.arlabdevelopments.firmador.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;




import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import es.arlabdevelopments.firmador.model.TipoCredencial;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificador; 

    @Column(nullable = false)
    private String password; 

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Credencial> credenciales = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String identificador, String password) {
        this.identificador = identificador;
        this.password = password;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Credencial> getCredenciales() {
        return credenciales;
    }

    public void setCredenciales(List<Credencial> credenciales) {
        this.credenciales = credenciales;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", identificador='" + identificador + '\'' +
                '}';
    }
}
