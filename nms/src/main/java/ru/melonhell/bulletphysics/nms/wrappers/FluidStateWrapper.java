package ru.melonhell.bulletphysics.nms.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FluidStateWrapper {
    private final Object handle;
    private final boolean isEmpty;
}
