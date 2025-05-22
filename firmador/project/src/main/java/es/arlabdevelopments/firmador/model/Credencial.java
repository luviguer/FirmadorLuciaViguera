package es.arlabdevelopments.firmador.model;

import jakarta.persistence.*;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import es.arlabdevelopments.firmador.model.TipoCredencial;


@Entity
@Table(name = "credenciales",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "tipo"}))
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCredencial tipo; 

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contenidoJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Credencial() {
    }

    public Credencial(TipoCredencial tipo, String contenidoJson, Usuario usuario) {
        this.tipo = tipo;
        this.contenidoJson = contenidoJson;
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCredencial getTipo() {
        return tipo;
    }

    public void setTipo(TipoCredencial tipo) {
        this.tipo = tipo;
    }

    public String getContenidoJson() {
        return contenidoJson;
    }

    public void setContenidoJson(String contenidoJson) {
        this.contenidoJson = contenidoJson;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Credencial{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", usuarioId=" + (usuario != null ? usuario.getId() : "null") +
                '}';
    }
}
