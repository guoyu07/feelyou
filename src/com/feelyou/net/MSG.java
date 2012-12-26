package com.feelyou.net;

public class MSG {
    /**extract type*/
    public static final int BYTE=1;
    /**extract type*/
    public static final int CHAR=2;
    /**Message bag start tag*/
    public static final byte head = (byte)0xd6;
    /**Message bag length*/
    private int length;
    /**message body length*/
    private int mlength;
    /**Message bag type*/
    private byte type;
    /**Message body*/
    private byte[] message;
    /**Message bag end tag*/
    private static final byte end = (byte)0x6b;
    /**message bag end tag for new version*/
    private static final byte endn = (byte)0x6d;
    /**start postion*/
    private int postion;
    /**message version. 0- mobile, 1-im*/
    private int version;
    /** Creates a new instance of Message */
    public MSG() {
        this.type = 0;
        this.length = 0;
        this.mlength = 0;
    }
    
    /**
     * update Message package
     */
    private void update(){
        prepare(0);
        //set package head
        this.message[0] = MSG.head;
        //update package length
        this.length = this.mlength+7;
        this.message[1] = (byte)(this.length%256);
        this.message[2] = (byte)(this.length>>8);
        this.message[3] = this.type;
        this.message[4] = (byte)(this.mlength%256);
        this.message[5] = (byte)(this.mlength>>8);
        //do not change package content
        //set package end
        this.message[this.length-1]=this.version==0 ?MSG.end:MSG.endn;
    }
    
    /**get message version. this method have some bad actions: class need init(decode or update), else bas version return.
     * 0 - mobile version
     * 1 - version
     * @return message version*/
    public int getVersion(){
        return this.version;
    }
    /**set message version. 0- mobile, 1-im*/
    public void setVersion(int ver){
        if(ver>-1 && ver <2){
            this.version =ver;
        }
    }
    
    /**Modify type
     * @param type reset type*/
    public void setType(int type){
        this.type=(byte)type;
    }
    
    /**to string
     * @return please use getMessage method*/
    public String toString(){
        return "Message:Please use getMessage method";
    }
    
    /**
     * Return the result plused head and end, used when send
     * 
     * @return the whole Message
     */
    public byte[] getMessage(){
        update();
        int xlength = this.length;
        if(xlength % 4!=0) {
            xlength += 4-(xlength%4);
        } 
        byte[] ret = new byte[xlength];
        System.arraycopy(this.message,0,ret,0,this.length);
        return ret;
    }
    
    /**
     * Get Message body, used when handle respons
     * 
     * @return the only body of Message
     */
    public byte[] getBody(){
        prepare(0);
        byte[] ret = new byte[mlength];
        System.arraycopy(message,6,ret,0,mlength);
        return ret;
    }
    
    /**
     * Get Message type, handle respons
     * 
     * @return the type of Message
     */
    public byte getType(){
        return this.type;
    }
    
    /**
     * Get Message body length
     * 
     * @return the length of Message
     */
    public int getLength(){
        int x=this.mlength+7;
        int patch=x & 0x000000003;
        if(patch >0){
            x+=4-patch;
        }
        return x;
    }
    
    /**
     * Append Message data
     * 
     * @param MSG append byte[] Message
     */
    public void append(byte[] message){
        if(message == null){return;}
        prepare(message.length);
        System.arraycopy(message, 0, this.message, this.mlength+6, message.length);
        this.mlength+=message.length;
    }
    
    /**
     * Append Message data
     * 
     * @param MSG append byte Message
     */
    private void appendb(byte message){
        prepare(1);
        byte[] app = new byte[1];
        app[0] = message;
        append(app);
        app = null;
    }
    
    
    /**
     * Append Message data
     * 
     * @param MSG append char Message
     */
    private void appendc(char message){
        prepare(2);
        byte[] app = new byte[2];
        app[0] = (byte)(message%256);
        app[1] = (byte)(message/256);
        append(app);
        app = null;
    }
    
    /**
     * Append one (byte)field
     * 
     * @param MSG append Message with (byte)head
     */
    public void appendByte(byte[] value){
        if(value == null){return;}
        prepare(value.length+1);
        appendb((byte)value.length);
        append(value);
    }
    
    /**
     * Append one (char)field
     * 
     * @param MSG append Message with (byte)head
     */
    public void appendChar(byte[] value){
        if(value == null){return;}
        prepare(value.length+2);
        appendc((char)value.length);
        append(value);
    }
    
    /**Extract on (byte)field*/
    public byte[] extract(final int length_type){
        byte[] ret=new byte[0];
        byte[] xmessage = getBody();
        int xlength=0;
        if(postion < 0) {
            System.out.println("postion error");
            return ret;
        }
        if(postion +length_type -1< xmessage.length) {//must dec end
            switch(length_type) {
                case BYTE:
                    xlength = (char)xmessage[postion]&0x0ffff;
                    break;
                case CHAR:
                    if(postion < xmessage.length-1) {
                        xlength = (char)(((char)(xmessage[postion+1]&0x0ff)<<8) + (char)(xmessage[postion]&0x0ff));
                    } else {
                        postion = -1;
                    }
                    break;
                default:
                    xlength =0;
                    return ret;
            }
        } else {
            postion = -1;
            return ret;
        }
        if(postion+xlength < xmessage.length && xlength<10000) {
            ret = null;
            ret = new byte[xlength];
            postion += length_type;
            try{
                System.arraycopy(xmessage,postion,ret,0,xlength);
            } catch (ArrayIndexOutOfBoundsException ex){
                System.out.println(ex.getMessage());
                postion = -1;
                return new byte[0];
            }
            postion += xlength;
        }
        xmessage = null;
        return ret;
    }
    
    /**reset postion of extract*/
    public void reset(){
        postion = 0;
    }
    
    /**
     * decode Message data
     * 
     * @param MSG decode Message
     */
    public void decode(byte[] message){
        if(message.length>7){
            this.length = (int)(message[1]&0x0ff)+(int)(message[2]&0x0ff)*256;
            this.type=message[3];
            this.mlength = ((message[5]&0x0ff)<<8)+(message[4]&0x0ff);
            if(mlength > length -7) {
                mlength = 0;
            }
            this.message = new byte[this.length];
            System.arraycopy(message,0,this.message,0,this.length);
            postion =0;
        }
        this.version = message[length-1]==0x6b?0:1;
    }
    
    /**
     * clean all Message
     */
    public void clean(){
        mlength=0;
        message=null;
    }
    
    /**close message resource
     */
    public void dispose(){
        this.mlength=0;
        this.message=null;
    }
    
    /***/
    private void prepare(int size){
        boolean rebuild = false;
        if(message == null){
            if(size>>1 > 256){
                size+=512;
            }else{
                size=512;
            }
        }else{
            int tmp=this.mlength+size+10;
            if(this.message.length < tmp){
                size = tmp;
                rebuild = true;
            }
        }
        if(rebuild){
            byte[] tmp = this.message;
            this.message = new byte[size];
            System.arraycopy(tmp, 0, message, 0, tmp.length);
            tmp = null;
        }else{
            if(this.message == null){
                this.message = new byte[size];
            }
        }
    }
}
