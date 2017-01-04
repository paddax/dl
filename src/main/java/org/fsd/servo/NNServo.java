package org.fsd.servo;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter Davis on 01/01/2017.
 */
public class NNServo {

    private static int seed = 123;
    private static double learningRate = 0.9;
    private static int numHiddenNodes = 12;
    private static int numOutputs = 1; // servo
    private static int numInputs = 3; // error / velocity / acceleration

    public static void main(String[] args) throws IOException, InterruptedException {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(learningRate)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.TANH).build())
                .layer(1, new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.TANH).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                        .build();



        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates

        int batchSize = 1;
        int nEpochs = 30;


        DataSetIterator trainIter = getTrainingData(batchSize);
        for ( int n = 0; n < nEpochs; n++) {
            model.fit( trainIter );
        }

    }

    private static DataSetIterator getTrainingData(int batchSize) throws IOException, InterruptedException {
        RecordReader rr = new CSVRecordReader();
//        rr.initialize(new FileSplit(new File("src/main/resources/classification/linear_data_train.csv")));
        rr.initialize(new FileSplit(new File("data.csv")));
        INDArray label = Nd4j.zeros(2000, 1);
        INDArray data =  Nd4j.zeros(2000, 3);
        int row = 0;
        while(rr.hasNext()) {
            List<Writable> x = rr.next();
            label.add(1);
            label.putScalar(new int[]{row}, x.get(0).toDouble());
            data.putScalar(new int[] {row, 0}, x.get(1).toDouble());
            data.putScalar(new int[] {row, 1}, x.get(2).toDouble());
            data.putScalar(new int[] {row, 2}, x.get(3).toDouble());
            row++;
            if(row == 2000)
                break;
        }
        DataSet allData = new DataSet(data, label);

        final List<DataSet> list = allData.asList();
        return new ListDataSetIterator(list,batchSize);

    }
}
