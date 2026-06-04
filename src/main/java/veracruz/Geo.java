package veracruz;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
/**
 *
 * @author ferna
 */
public final class Geo {
    private Geo(){}

    static int[] box(GL2 gl, VeracruzDemo d, float w,float h,float dp, float r,float g,float b){
        float hw=w/2,hh=h/2,hd=dp/2;
        float[] v={
            -hw,-hh,hd,0,0,1,r,g,b, hw,-hh,hd,0,0,1,r,g,b, hw,hh,hd,0,0,1,r,g,b, -hw,hh,hd,0,0,1,r,g,b,
            hw,-hh,-hd,0,0,-1,r,g,b, -hw,-hh,-hd,0,0,-1,r,g,b, -hw,hh,-hd,0,0,-1,r,g,b, hw,hh,-hd,0,0,-1,r,g,b,
            hw,-hh,hd,1,0,0,r,g,b, hw,-hh,-hd,1,0,0,r,g,b, hw,hh,-hd,1,0,0,r,g,b, hw,hh,hd,1,0,0,r,g,b,
            -hw,-hh,-hd,-1,0,0,r,g,b, -hw,-hh,hd,-1,0,0,r,g,b, -hw,hh,hd,-1,0,0,r,g,b, -hw,hh,-hd,-1,0,0,r,g,b,
            -hw,hh,hd,0,1,0,r,g,b, hw,hh,hd,0,1,0,r,g,b, hw,hh,-hd,0,1,0,r,g,b, -hw,hh,-hd,0,1,0,r,g,b,
            -hw,-hh,-hd,0,-1,0,r,g,b, hw,-hh,-hd,0,-1,0,r,g,b, hw,-hh,hd,0,-1,0,r,g,b, -hw,-hh,hd,0,-1,0,r,g,b,
        };
        int[] idx=new int[36];
        for(int f=0;f<6;f++){int base=f*4,j=f*6;idx[j]=base;idx[j+1]=base+1;idx[j+2]=base+2;idx[j+3]=base+2;idx[j+4]=base+3;idx[j+5]=base;}
        return d.upload(gl,v,idx);
    }

    static int[] ground(GL2 gl, VeracruzDemo d, float w,float dp, float r,float g,float b){
        float s=w/2,t=dp/2;
        float[] v={-s,0,t,0,1,0,r,g,b, s,0,t,0,1,0,r,g,b, s,0,-t,0,1,0,r,g,b, -s,0,-t,0,1,0,r,g,b};
        int[] idx={0,1,2, 2,3,0};
        return d.upload(gl,v,idx);
    }

    static int[] sphere(GL2 gl, VeracruzDemo d, float rad,int sl,int st, float r,float g,float b){
        float[] v=new float[(st+1)*(sl+1)*9]; int vi=0;
        for(int s=0;s<=st;s++){
            double phi=Math.PI*s/st-Math.PI/2; float ny=(float)Math.sin(phi),cr=(float)Math.cos(phi);
            for(int l=0;l<=sl;l++){
                double th=2*Math.PI*l/sl; float nx=(float)(cr*Math.cos(th)),nz=(float)(cr*Math.sin(th));
                v[vi++]=rad*nx;v[vi++]=rad*ny;v[vi++]=rad*nz; v[vi++]=nx;v[vi++]=ny;v[vi++]=nz; v[vi++]=r;v[vi++]=g;v[vi++]=b;
            }
        }
        int[] idx=new int[st*sl*6]; int ii=0;
        for(int s=0;s<st;s++) for(int l=0;l<sl;l++){
            int tl=s*(sl+1)+l,tr=tl+1,bl=tl+sl+1,br=bl+1;
            idx[ii++]=tl;idx[ii++]=bl;idx[ii++]=tr; idx[ii++]=tr;idx[ii++]=bl;idx[ii++]=br;
        }
        return d.upload(gl,v,idx);
    }

    static int[] cyl(GL2 gl, VeracruzDemo d, float rad,float h,int seg, float r,float g,float b){
        float hh=h/2; int ring=seg+1;
        float[] v=new float[(ring*2+2)*9]; int vi=0;
        for(int s=0;s<=seg;s++){
            double a=2*Math.PI*s/seg; float nx=(float)Math.cos(a),nz=(float)Math.sin(a),x=rad*nx,z=rad*nz;
            v[vi++]=x;v[vi++]=-hh;v[vi++]=z; v[vi++]=nx;v[vi++]=0;v[vi++]=nz; v[vi++]=r;v[vi++]=g;v[vi++]=b;
            v[vi++]=x;v[vi++]=hh;v[vi++]=z; v[vi++]=nx;v[vi++]=0;v[vi++]=nz; v[vi++]=r;v[vi++]=g;v[vi++]=b;
        }
        int bc=ring*2,tc=bc+1;
        v[vi++]=0;v[vi++]=-hh;v[vi++]=0; v[vi++]=0;v[vi++]=-1;v[vi++]=0; v[vi++]=r;v[vi++]=g;v[vi++]=b;
        v[vi++]=0;v[vi++]=hh;v[vi++]=0; v[vi++]=0;v[vi++]=1;v[vi++]=0; v[vi++]=r;v[vi++]=g;v[vi++]=b;
        int[] idx=new int[seg*12]; int ii=0;
        for(int s=0;s<seg;s++){int b0=s*2,t0=b0+1,b1=b0+2,t1=b0+3;idx[ii++]=b0;idx[ii++]=b1;idx[ii++]=t0;idx[ii++]=t0;idx[ii++]=b1;idx[ii++]=t1;}
        for(int s=0;s<seg;s++){idx[ii++]=bc;idx[ii++]=s*2+2;idx[ii++]=s*2;}
        for(int s=0;s<seg;s++){idx[ii++]=tc;idx[ii++]=s*2+1;idx[ii++]=s*2+3;}
        int[] tr=new int[ii]; System.arraycopy(idx,0,tr,0,ii);
        return d.upload(gl,v,tr);
    }

    static void addBox(GL2 gl, VeracruzDemo d, float x,float y,float z, float w,float h,float dp, float r,float g,float b){
        d.add(box(gl,d,w,h,dp,r,g,b), new Matrix4f().translate(x,y,z));
    }
    static void addCyl(GL2 gl, VeracruzDemo d, float x,float y,float z, float rad,float h,int seg, float r,float g,float b){
        d.add(cyl(gl,d,rad,h,seg,r,g,b), new Matrix4f().translate(x,y,z));
    }
    static void addSphere(GL2 gl, VeracruzDemo d, float x,float y,float z, float rad,int sl,int st, float r,float g,float b){
        d.add(sphere(gl,d,rad,sl,st,r,g,b), new Matrix4f().translate(x,y,z));
    }
}
