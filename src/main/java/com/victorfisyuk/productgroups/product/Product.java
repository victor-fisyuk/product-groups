package com.victorfisyuk.productgroups.product;

import com.victorfisyuk.productgroups.group.Group;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@DynamicUpdate
@Entity
@SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", allocationSize = 1)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @NotBlank
    private String name;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public Product(String name) {
        this.name = name;
    }
}
