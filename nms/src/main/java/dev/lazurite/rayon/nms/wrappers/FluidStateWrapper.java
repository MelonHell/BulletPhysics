package dev.lazurite.rayon.nms.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FluidStateWrapper {
    private final Object handle;
    private boolean isEmpty;
}
