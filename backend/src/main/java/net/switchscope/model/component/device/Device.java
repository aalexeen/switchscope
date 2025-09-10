package net.switchscope.model.component.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.switchscope.model.NamedEntity;
import net.switchscope.model.component.Component;
import net.switchscope.model.location.Location;
import net.switchscope.model.port.Port;
import net.switchscope.validation.NoHtml;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DEVICE")
public abstract class Device extends Component implements HasPorts {

    // Специфичные для устройств поля
    @Column(name = "management_ip")
    private String managementIp;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Port> ports = new ArrayList<>();

    @Override
    public boolean canHoldOtherComponents() {
        return false; // Устройства обычно не содержат другие компоненты
    }

    @Override
    public boolean requiresPhysicalSpace() {
        return true;
    }

    // Device-специфичные методы
    public abstract boolean isManaged();
    public abstract String getDefaultProtocol();
}