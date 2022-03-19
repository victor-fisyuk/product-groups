package com.victorfisyuk.productgroups.group;

import com.victorfisyuk.productgroups.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@DynamicUpdate
@Entity
@Table(name = "groups")
@SequenceGenerator(name = "group_sequence", sequenceName = "group_sequence", allocationSize = 1)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_sequence")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @NotBlank
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Product> products = new ArrayList<>();

    @ToString.Exclude
    @ElementCollection
    @Column(name = "tag")
    @Setter(AccessLevel.NONE)
    private Set<String> tags = new HashSet<>();

    public Group(String name, Collection<Product> products) {
        this.name = name;
        this.products.addAll(products);
        this.products.forEach(product -> product.setGroup(this));
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setGroup(this);
    }
}
