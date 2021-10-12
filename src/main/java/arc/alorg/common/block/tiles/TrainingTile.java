package arc.alorg.common.block.tiles;

import arc.alorg.common.controller.XorgTraining;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

public class TrainingTile extends ALOrgTile implements ITickableTileEntity {
    private static final int ENVIRONMENT_SIZE = 19;

    private XorgTraining training;

    public TrainingTile() {
        super(ModTiles.TRAINING);
    }

    public XorgTraining getTraining() {
        return training;
    }

    public void startTraining(int id) {
        if (!level.isClientSide()) {
            if (training == null) {

                BlockPos startPos = getBlockPos().south();
                BlockPos endPos = startPos.offset(ENVIRONMENT_SIZE, ENVIRONMENT_SIZE, ENVIRONMENT_SIZE);

                training = new XorgTraining(ENVIRONMENT_SIZE, this, startPos, endPos, id);
            }

            training.setupEnvironment();

            training.runTraining();
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        if (training != null) {
            training.writePacketNBT(cmp);
        }
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        if (training != null) {
            training.readPacketNBT(cmp);
        }
    }
}
