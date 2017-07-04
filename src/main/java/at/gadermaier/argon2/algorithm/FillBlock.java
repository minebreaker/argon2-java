package at.gadermaier.argon2.algorithm;

import at.gadermaier.argon2.model.Block;

class FillBlock {

    static void fillBlock(Block prevBlock, Block refBlock, Block nextBlock, boolean withXor) {

        Block blockR = new Block();
        Block blockTmp = new Block();

        blockR.copyBlock(refBlock);
        blockR.xorBlock(prevBlock);
        blockTmp.copyBlock(blockR);

        if (withXor) {
            blockTmp.xorBlock(nextBlock);
        }

        /* Apply Blake2 on columns of 64-bit words: (0,1,...,15) , then
        (16,17,..31)... finally (112,113,...127) */
        for (int i = 0; i < 8; ++i) {

            Functions.roundFunction(blockR,
                    16 * i, 16 * i + 1, 16 * i + 2,
                    16 * i + 3, 16 * i + 4, 16 * i + 5,
                    16 * i + 6, 16 * i + 7, 16 * i + 8,
                    16 * i + 9, 16 * i + 10, 16 * i + 11,
                    16 * i + 12, 16 * i + 13, 16 * i + 14,
                    16 * i + 15
            );
        }

        /* Apply Blake2 on rows of 64-bit words: (0,1,16,17,...112,113), then
        (2,3,18,19,...,114,115).. finally (14,15,30,31,...,126,127) */
        for (int i = 0; i < 8; i++) {

            Functions.roundFunction(blockR,
                    2 * i, 2 * i + 1, 2 * i + 16,
                    2 * i + 17, 2 * i + 32, 2 * i + 33,
                    2 * i + 48, 2 * i + 49, 2 * i + 64,
                    2 * i + 65, 2 * i + 80, 2 * i + 81,
                    2 * i + 96, 2 * i + 97, 2 * i + 112,
                    2 * i + 113
            );

            nextBlock.copyBlock(blockTmp);
            nextBlock.xorBlock(blockR);
        }
    }
}
