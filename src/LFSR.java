public class LFSR {
    private static int register=0;

    public static void main(String[] args) {
        byte[] initialSeed = {(byte) 0b10101010, (byte) 0b11110000, (byte) 0b00001111, (byte) 0b01010101};
        register = initRegister(initialSeed);

        byte[] array=byteArrayGenerator(20);
        System.out.println(getHexStringFromByteArray(array));

        byte[] array2=byteArrayGenerator(50);
        System.out.println(getHexStringFromByteArray(array2));
    }

    private static byte[] byteArrayGenerator(int arraySize){
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

    private static byte fullStep(){
        byte leastBit = getLeastSignificantBit(register);
        register = shiftAndInsertTapBit(applyTapSequence(register));
        return leastBit;
    }

    private static int shiftAndInsertTapBit(byte tapBit) {
        register = register >>> 1;
        register = register | (tapBit << 31);
        return register;
    }

    private static byte getLeastSignificantBit(int register) {
        return (byte) (register & 1);
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

    private static void printRegister(int register) {
        System.out.println("Register (binary): "+Integer.toBinaryString(register));
        System.out.println("Register (hex): "+Integer.toHexString(register).toUpperCase());
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
