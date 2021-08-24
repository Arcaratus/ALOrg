package arc.alorg.common.controller;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class XorgState implements Encodable {
    private final INDArray data;

//    private final double dx, dy, dz;
//    private final double distance;

    public XorgState(double dx, double dy, double dz, double distance) {
//        this.dx = dx;
//        this.dy = dy;
//        this.dz = dz;
//        this.distance = distance;
        this.data = Nd4j.createFromArray(dx, dy, dz, distance);
    }

    private XorgState(INDArray data) {
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