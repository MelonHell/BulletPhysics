package dev.lazurite.rayon.nms.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuaternionWrapper {
    private final float i;
    private final float j;
    private final float k;
    private final float r;
}
