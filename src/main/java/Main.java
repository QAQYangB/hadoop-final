public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
//        // training
//		NBClassifier.train();
		long endTime = System.currentTimeMillis();

        System.out.println("训练样本用时: "+ (endTime - startTime)/1000L/60L+"分钟");

        // test
        NBClassifier.loadModel();
        System.out.println(NBClassifier.predict("几乎 凌晨 才 到 包头 包头 没有 什么 特别 好 酒店 每次 来 就是 住 这家 所以 没有 忒 多 对比 感觉 行 下次 还是 得到 这里 来 住"));
        NBClassifier.predictAll();
    }
}
