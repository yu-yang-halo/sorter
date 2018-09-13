package th.service.helper;


import com.yy.sorter.utils.ConvertUtils;

import java.util.Arrays;


/**
 * 
 * @author Administrator
 * 
 *         定义发送与接受包的数据结构  包头 15byte    扩展数据  最大2048
 *         type	    ex_type 	Data1	number	len	     crc	ex_data
 *         1byte	 1byte	    8byte	1byte	2byte	2byte	0-2048 byte
 *         len 表示ex_data 长度
 *         校验：所有数据的CRC校验和，暂不使用
 *         一条协议最少发送15个字节
 *
 */
public class ThPackage {
	public static final int PACKET_HEADER_SIZE=15;
	private byte type; // 类型
	private byte extendType; // 扩展类型
	private byte[] data1;    //发送数据 8个bytes
	private byte packetNumber; // 包号
	private int len;//发送的数据或接收的数据长度 short是有符号会出现负数
	private int crc; // crc校验
	private byte[] contents;

	private PacketExt packetExt;//扩展


	/**
	 * 接收的数据包总长度
	 */
	private int length;
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public String getSenderIP() {
		return senderIP;
	}
	public void setSenderIP(String senderIP) {
		this.senderIP = senderIP;
	}
	private String senderIP;
	
	
	public ThPackage(){
		
	}

	public ThPackage(byte type, byte extendType, byte[] data1, byte packetNumber, int len, short crc, byte[] contents) {
		this.type = type;
		this.extendType = extendType;

		this.data1=new byte[8];
		if(data1!=null){
			if(data1.length<=8)
			{
				System.arraycopy(data1,0,this.data1,0,data1.length);
			}else
			{
				System.arraycopy(data1,0,this.data1,0,this.data1.length);
			}
		}

		this.len = len;
		this.crc = (short) 0xabba;
		this.contents = contents;
		if(contents!=null){
			this.len=(short) contents.length;
		}

	}

	public ThPackage(byte[] all,int length) {
		super();
		
		if(length>=PACKET_HEADER_SIZE){
			this.length=length;
			
			this.type = all[0];
			this.extendType = all[1];

			this.data1=new byte[8];

			System.arraycopy(all,2,data1,0,8);
			this.packetNumber=all[10];

			this.len= (((all[11]&0xFF)<<8)|(all[12]&0xFF));
			this.crc= (((all[13]&0xFF)<<8)|(all[14]&0xFF));

			
			int conentLength=this.length-PACKET_HEADER_SIZE;

			if(len!=conentLength){
				len=  conentLength;
			}
			
			this.contents=new byte[conentLength];
			System.arraycopy(all, PACKET_HEADER_SIZE, contents, 0, conentLength);
			
		}
		
		
	}


	/**
	 * 待发送的包数据
	 * @return
     */
	public byte[] myByteArrays(){
		//除去内容 数据包长度为26
		
		int length=PACKET_HEADER_SIZE;
		if(contents!=null){
			length+=contents.length;
			
		}
		byte[] all=new byte[length];
		all[0]=type;
		all[1]=extendType;
		System.arraycopy(data1,0,all,2,8);

		all[10]=packetNumber;



		for(int i=0;i<2;i++){
			all[11+i]=(byte) ((len>>8*(1-i))&0xFF);
		}
		for(int i=0;i<2;i++){
			all[13+i]=(byte) ((crc>>8*(1-i))&0xFF);
		}


		if(contents!=null){
			System.arraycopy(contents, 0, all, PACKET_HEADER_SIZE, contents.length);
		}

		ThLogger.debug("myByteArrays:: ",toString());
		return all;
	}

	@Override
	public String toString() {
		return "ThPackage{" +
				"type=" + Integer.toHexString(ConvertUtils.unsignByteToInt(type)) +
				", extendType=" + Integer.toHexString(extendType) +
				", data1=" + Arrays.toString(data1) +
				", packetNumber=" + packetNumber +
				", len=" + len +
				", crc=" + crc +
				", contents=" + Arrays.toString(contents) +
				", ****length=" + length +
				", senderIP='" + senderIP + '\'' +
				'}';
	}

	public PacketExt getPacketExt() {
		return packetExt;
	}

	public void setPacketExt(PacketExt packetExt) {
		this.packetExt = packetExt;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getExtendType() {
		return extendType;
	}

	public void setExtendType(byte extendType) {
		this.extendType = extendType;
	}

	public byte[] getData1() {
		return data1;
	}

	public void setData1(byte[] data1) {
		this.data1 = data1;
	}

	public byte getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(byte packetNumber) {
		this.packetNumber = packetNumber;
	}

	public int getLen() {
		return len;
	}

	public void setLen(short len) {
		this.len = len;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}
}
