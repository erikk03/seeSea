package gr.uoa.di.ships.persistence.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("2")
@Table(name = "administrator")
public class Administrator extends RegisteredUser {

}
