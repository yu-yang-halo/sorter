package th.service.helper;

import th.service.core.IReceiveListenser;

/**
 * Created by Administrator on 2017/5/22.
 */

public class CircleBuffer {
    public static int RECEIVE_BUFFER_MIN=5*1024; //5k
    public static int RECEIVE_BUFFER_MAX=64*1024;//64k
    private int buffer_max=100*1024;//100k
    private byte[] buf=new byte[buffer_max];
    private byte[] recv_buf=new byte[80*1024];   //能够容纳最长包 包头+data  80k
    private int packHeadLengh=15;
    private int head=0;
    private int rear=0;

    /**
     * 清空缓存
     */
    public void reset(){
        head=0;
        rear=0;
    }

    private IReceiveListenser iReceiveListenser;
    //可用空间
    private int getFreeBuflength()
    {
        return buffer_max-getDataLength();
    }

    private boolean isEmpty()
    {
        return head==rear;
    }

    public IReceiveListenser getiReceiveListenser() {
        return iReceiveListenser;
    }

    public void setiReceiveListenser(IReceiveListenser iReceiveListenser) {
        this.iReceiveListenser = iReceiveListenser;
    }



    //已经存放的数据长度
    private int getDataLength()

    {
        if(rear >= head)
            return rear - head;
        else
        {
            return rear-head + buffer_max;
        }
    }


    public void copyData(byte[] data,int len )
    {
        for(int i=0;i<len;i++)
        {
            buf[rear] = data[i];
            rear = (rear + 1)% buffer_max;
        }
    }

    //存放数据进缓冲区，如果自由空间不够，重新开一个大的缓冲区
//将原先缓冲区中的数据，移至现有缓冲区，更新索引，缓冲区大小
//释放原先缓冲区，将缓冲区指针指向新开缓冲区
    public void pushData(byte[] data,int len ) throws Exception
    {
        if(getFreeBuflength() > len)
        {
            copyData(data,len);
        }
        else
        {
            //开辟更大空间，将原有数据复制到新的缓存
            byte[] p=new byte[buffer_max + len];
            int  data_len = getDataLength();
            for(int i=0 ;i<data_len ;i++)
            {
                p[i] = buf[head];
                head = (head + 1)% buffer_max;
            }

            //设备新缓存的数据
            head = 0;
            rear = data_len;
            buffer_max = buffer_max +len;
            buf =p;
            //拷贝数据
            copyData(data,len);
        }
        processData();
    }

    //取数据到接收缓存区中，头索引不移动，
//取完后，如果有完整体的数据时，再移动头索引
   private void getData(int len )
    {
        int tmp = head;
        for(int i=0;i<len ;i++)
        {
            recv_buf[i] = buf[tmp];
            tmp = (tmp + 1)% buffer_max;
        }
    }

    private void moveHead( int len )
    {
        head = (head + len)% buffer_max;
    }

    //按协议处理TCP数据,传入一个处理函数指针，用于回调
   private void processData() throws Exception{
       while (getDataLength() >= packHeadLengh)               //当数据不少于一个包头时
       {
           getData(packHeadLengh);                                //获得包头数据//解出包头
           if (recv_buf[13] == (byte) 0xab && recv_buf[14] == (byte) 0xba)      //数据是否合法
           {
               int data_len = ((recv_buf[11]&0xFF) << 8 )| (recv_buf[12] & 0xFF);

               if (data_len == 0)                                  //只有包头，没有数据
               {

                   /**
                    * 解析出一个完整包（没有数据）recv_buf,packHeadLengh
                    */
                   if(iReceiveListenser!=null){
                       System.out.println("data_len "+data_len);
                       iReceiveListenser.onReadData(recv_buf,packHeadLengh);
                   }

                   moveHead(packHeadLengh);
               } else {
                   if (getDataLength() >= (data_len + packHeadLengh))    //有包头，有数据
                   {

                       getData(data_len + packHeadLengh);
                       moveHead(data_len + packHeadLengh);
                       /**
                        * 解析出一个完整包（有数据）recv_buf,data_len+packHeadLengh
                        */
                        if(iReceiveListenser!=null){
                            System.out.println("data_len "+data_len);
                            iReceiveListenser.onReadData(recv_buf,data_len+packHeadLengh);
                        }

                   } else
                       break;            //不处理
               }
           } else {
               reset();
               //出错，由上层处理
               throw new Exception("协议解析异常,需要关闭连接");
           }

       }
   }
}
