package com.wjy35.fileserver;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Chunk {
    byte[] data;
    long fileLength;
}
