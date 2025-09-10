package net.switchscope.model.port;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.component.device.Device;

/**
 * Заглушка для класса Port
 * Полная реализация будет создана отдельно согласно схеме
 */
@Entity
@Table(name = "ports")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "port_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Port extends NamedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Device device;

    // Other fields will be added during full implementation
    // according to schema: status, speed, lastChange, etc.

    protected Port(Integer id, String name) {
        super(id, name);
    }
}
