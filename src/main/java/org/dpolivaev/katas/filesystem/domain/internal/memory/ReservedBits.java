package org.dpolivaev.katas.filesystem.domain.internal.memory;

class ReservedBits {
    private static final int NOT_AVAILABLE = -1;
    private final Memory memory;

    ReservedBits(Memory memory) {
        this.memory = memory;
    }

    long reserveBit() {
        long passedBits = 0;
        for(long blockIndex = 0; blockIndex < memory.blockCount(); blockIndex ++){
            DataBlock block = memory.at(blockIndex);
            long positionInBlock = reserveBit(block);
            if(positionInBlock != NOT_AVAILABLE)
                return passedBits + positionInBlock;
            else
                passedBits += block.size() * Byte.SIZE;
        }
        throw new OutOfMemoryException("No bits available");
    }

    private long reserveBit(DataBlock block) {
        for(long byteIndex = 0; byteIndex < block.size(); byteIndex++){
            byte candidate = block.getByte(byteIndex);
            if(candidate != NOT_AVAILABLE) {
                return reserveAvailableBit(block, byteIndex, candidate);
            }
        }
        return NOT_AVAILABLE;
    }

    private long reserveAvailableBit(DataBlock block, long byteIndex, byte candidate) {
        int modificaton = 1;
        for(int bitPosition = 0; bitPosition < Byte.SIZE; bitPosition++) {
            int modifiedBit = (candidate | modificaton);
            if(modifiedBit != candidate) {
                block.put(byteIndex, (byte)modifiedBit);
                return byteIndex * Byte.SIZE + bitPosition;
            }
            else
                modificaton <<= 1;
        }
        throw new IllegalArgumentException("No available bit found");
    }

    void releaseBit(long position){

    }
}
