public class NMSProvider {
    public static NMS getInstance() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        switch(version) {
            case "v1_8_R2": return new NMS_V1_8_R2();
            case "v1_8_R3": return new NMS_V1_8_R3();
            case "v1_9_R1": return new NMS_V1_9_R1();
            case "v1_9_R2": return new NMS_V1_9_R2();
            case "v1_10_R1": return new NMS_V1_10_R1();
            case "v1_11_R1": return new NMS_V1_11_R1();
            case "v1_12_R1": return new NMS_V1_12_R1();
            case "v1_13_R1": return new NMS_V1_13_R1();
            case "v1_13_R2": return new NMS_V1_13_R2();
            case "v1_14_R1": return new NMS_V1_14_R1();
            case "v1_15_R1": return new NMS_V1_15_R1();
            case "v1_16_R1": return new NMS_V1_16_R1();
            case "v1_16_R2": return new NMS_V1_16_R2();
            case "v1_16_R3": return new NMS_V1_16_R3();
            case "v1_17_R1": return new NMS_V1_17_R1();
            case "v1_18_R1": return new NMS_V1_18_R1();
            case "v1_18_R2": return new NMS_V1_18_R2();
            case "v1_19_R1": return new NMS_V1_19_R1();
            case "v1_19_R2": return new NMS_V1_19_R2();
            case "v1_19_R3": return new NMS_V1_19_R3();
            case "v1_20_R1": return new NMS_V1_20_R1();
            case "v1_20_R2": return new NMS_V1_20_R2();
            case "v1_20_R3": return new NMS_V1_20_R3();
            case "v1_20_R4": return new NMS_V1_13_R4();



            // ... other versions
            default: return new NMSDefault();
        }
    }
}