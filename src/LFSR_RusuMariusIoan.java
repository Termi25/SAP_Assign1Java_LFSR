import java.util.BitSet;

public class LFSR_RusuMariusIoan {
    private static int registerInteger = 0;
    private static BitSet registerBitSet = new BitSet(32);

    public static void main(String[] args) {
        byte[] initialSeed = {(byte) 0b10101010, (byte) 0b11110000, (byte) 0b00001111, (byte) 0b01010101};

        System.out.println("\n1. LFSR with Integer implementation test:");
        System.out.println("--------------------------------------");

        registerInteger = initRegister(initialSeed);
        printRegister(registerInteger);

        System.out.println("\n20 byte generated value:");
        byte[] array= byteArrayGeneratorWithInteger(20);
        System.out.println(getHexStringFromByteArray(array));

        System.out.println("\n50 byte generated value:");
        byte[] array2= byteArrayGeneratorWithInteger(50);
        System.out.println(getHexStringFromByteArray(array2));

        System.out.println("--------------------------------------\n");

        System.out.println("\n2. LFSR with BitSet implementation test:");
        System.out.println("--------------------------------------\n");

        initRegisterBitSet(initialSeed);
        printRegisterBitSet(registerBitSet);

        System.out.println("\n20 byte generated value:");
        byte[] array3= byteArrayGeneratorWithBitSet(20);
        System.out.println(getHexStringFromByteArray(array3));

        System.out.println("\n50 byte generated value:");
        byte[] array4= byteArrayGeneratorWithBitSet(50);
        System.out.println(getHexStringFromByteArray(array4));

        System.out.println("--------------------------------------\n");

    }

    private static byte[] byteArrayGeneratorWithInteger(int arraySize){
        byte[] byteArray=new byte[arraySize];
        for(int i=0;i<arraySize;i++){
            int noBitsGenerated=0;
            byteArray[i]=0x00;
            while(noBitsGenerated<8){
                byte generatedBit=fullStep();
                byteArray[i]= (byte) (generatedBit<<noBitsGenerated | byteArray[i]);
                noBitsGenerated++;
            }
            byteArray[i]=(byte)(byteArray[i] & 0xFF);
        }
        return byteArray;
    }

    private static byte[] byteArrayGeneratorWithBitSet(int arraySize){
        byte[] byteArray=new byte[arraySize];
        for(int i=0;i<arraySize;i++){
            int noBitsGenerated=0;
            byteArray[i]=0x00;
            while(noBitsGenerated<8){
                byte generatedBit=fullStepBitSet();
                byteArray[i]= (byte) (generatedBit<<noBitsGenerated | byteArray[i]);
                noBitsGenerated++;
            }
            byteArray[i]=(byte)(byteArray[i] & 0xFF);
        }
        return byteArray;
    }

    private static byte fullStep(){
        byte leastBit = getLeastSignificantBit(registerInteger);
        registerInteger = shiftAndInsertTapBit(applyTapSequence(registerInteger));
        return leastBit;
    }

    private static byte fullStepBitSet(){
        byte leastBit=getLeastSignificantBitBitSet(registerBitSet);
        registerBitSet=shiftAndInsertTapBitBitSet(applyTapSequenceBitSet(registerBitSet));
        return leastBit;
    }

    private static int shiftAndInsertTapBit(byte tapBit) {
        registerInteger = registerInteger >>> 1;
        registerInteger = registerInteger | (tapBit << 31);
        return registerInteger;
    }

    private static BitSet shiftAndInsertTapBitBitSet(boolean tapBit){
        for(int i=0;i<31;i++){
            registerBitSet.set(i,registerBitSet.get(i+1));
        }
        registerBitSet.set(31,tapBit);
        return registerBitSet;
    }

    private static byte getLeastSignificantBit(int register) {
        return (byte) (register & 1);
    }

    private static byte getLeastSignificantBitBitSet(BitSet register){
        return register.get(0)? (byte)1:(byte)0;
    }

    private static byte applyTapSequence(int register) {
        //TAP sequence x^31 + x^7 + x^5 + x^3 + x^2 + x + 1;
        byte[] index = {31, 7, 5, 3, 2, 1, 0};
        byte result = 0;
        for (int i = 0; i < index.length; i++) {
            byte bitValue = (byte) (((1 << index[i]) & register) >>> index[i]);
            result = (byte) (result ^ bitValue);
        }
        return result;
    }

    private static boolean applyTapSequenceBitSet(BitSet register){
        //TAP sequence x^31 + x^7 + x^5 + x^3 + x^2 + x + 1;
        int[] index=new int[]{31,7,5,3,2,1,0};
        boolean result=false;
        for(int i=0;i<index.length;i++){
            boolean bitValue=register.get(index[i]);
            result=(result ^ bitValue);
        }
        return result;
    }

    private static int initRegister(byte[] initialValues) {
        if(initialValues.length!=4){
            System.out.println("Wrong initial value. 4 bytes required");
            return 0;
        }
        int result=0;
        for(int i=3;i>=0;i--){
            result=result | ((initialValues[3-i] & 0xFF )<< (i*8));
        }
        return result;
    }

    private static void initRegisterBitSet(byte[] initialValues){
        if(initialValues.length!=4){
            System.out.println("Wrong initial value. 4 bytes required");
        }else{
            for(int i=3;i>=0;i--){
                int contorBits=0;
                while(contorBits<8){
                    byte lsb= (byte) (initialValues[3-i] & 1);
                    if(lsb==1){
                        registerBitSet.set(i*8+contorBits);
                    }
                    initialValues[3-i]= (byte) (initialValues[3-i] >>>1);
                    contorBits++;
                }
            }
        }
    }

    private static void printRegister(int register) {
        System.out.println("Register (binary): "+Integer.toBinaryString(register));
        System.out.println("Register (hex): "+Integer.toHexString(register).toUpperCase());
    }

    private static void printRegisterBitSet(BitSet register) {
        System.out.print("Register (binary): ");
        for(int i=31;i>=0;i--){
            System.out.printf("%d",register.get(i) ? 1 : 0);
        }
        System.out.println();

        System.out.print("Register (hex): ");
        byte[] array=register.toByteArray();
        for(int i=3;i>=0;i--){
            System.out.printf("%02X",array[i]);
        }
        System.out.println();
    }

    private static String getHexStringFromByte(byte value){
        return String.format(" %02X",value);
    }

    private static String getHexStringFromByteArray(byte[] values){
        StringBuilder sb=new StringBuilder();
        for(byte value:values){
            sb.append(String.format(" %02X",value));
        }
        return sb.toString();
    }
}
