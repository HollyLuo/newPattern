package test;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class testModel {
    
    public static void main(String[] args) throws Exception {
        
        J48 tree = (J48) weka.core.SerializationHelper.read("/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/LogicModels/BehaviorLogic_[8, 7]_5_[6, 9]");
        
        Instances instancesTest = DataSource.read("/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/LogicModels/hasBehaviorLogic[6, 13, 20, 27, 42, 49].csv.arff"); // 读入测试文件  
//        Instances instancesTrain = DataSource.read("/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/ConceptCSV/hasBehaviorLogic[6, 13, 20, 27, 42, 49].csv"); // 读入测试文件  

        //        instancesTest.setClassIndex(instancesTest.numAttributes()-1);
//        
//        String trueClassLabel = 
//                instancesTest.instance(0).toString(instancesTest.classIndex());
//        
//        double sum = instancesTest.numInstances();
        
     // Make the prediction here.
//        System.out.println(instancesTrain.numAttributes());
//        instancesTrain.setClassIndex(instancesTrain.numAttributes()-1);
//        System.out.println(instancesTrain.classAttribute());
        
        instancesTest.setClassIndex(instancesTest.numAttributes()-1);
        System.out.println(instancesTest.classAttribute());
        System.out.println(instancesTest.instance(0));
        double predictionIndex = tree.classifyInstance(instancesTest.instance(0)); 
        System.out.println("predictionIndex:"+predictionIndex);

//         Get the predicted class label from the predictionIndex.
        String predictedClassLabel =
                instancesTest.classAttribute().value((int) predictionIndex);
        
        System.out.println(predictionIndex);
        System.out.println(predictedClassLabel);
        
        double[] dist = tree
                .distributionForInstance(instancesTest.instance(0));
//        System.out.println("dist: "+dist[0] + ";" + dist[1]);

        System.out.println(tree.graph());
        
        System.out.println(tree.prefix());
        
        System.out.println(instancesTest.toString());
        
//        while(instancesTest.enumerateInstances().hasMoreElements()){
//            System.out.println(instancesTest.enumerateInstances().nextElement());
//        }
        System.out.println(instancesTest.attribute(0));
        System.out.println(instancesTest.attribute(9));
        
        System.out.println(instancesTest.attribute(9).toString());
        
//        System.out.println(tree.m_root);
        
                
    }

}
