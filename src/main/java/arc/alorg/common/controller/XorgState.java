package arc.alorg.common.controller;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class XorgState implements Encodable {
    private final INDArray data;

//    private final double dx, dy, dz;
    public final double distance;

    public XorgState(double dx, double dy, double dz, double distance,
//                     double nextX, double nextY, double nextZ,
                     double[] surrounding) {
//        this.dx = dx;
//        this.dy = dy;
//        this.dz = dz;
        this.distance = distance;
        this.data = Nd4j.concat(0, Nd4j.createFromArray(dx, dy, dz, distance
//                , nextX, nextY, nextZ
        ), Nd4j.createFromArray(surrounding));
    }

    private XorgState(INDArray data) {
        this.distance = data.getDouble(3);
        this.data = data.dup();
    }

    @Override
    public double[] toArray() {
        return data.data().asDouble();
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return data;
    }

    @Override
    public Encodable dup() {
        return new XorgState(data);
    }
}