package veracruz;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author ferna
 */
public class VeracruzDemo extends GLJPanel
        implements GLEventListener, KeyListener, MouseMotionListener, MouseWheelListener {

    private static final int SM = 8192;

    // Pasada 1: solo depth
    private static final String D_VERT = "#version 120\n" +
            "attribute vec3 aPos;\n" +
            "uniform mat4 uLSM, uModel;\n" +
            "void main(){\n" +
            "  gl_Position = uLSM * uModel * vec4(aPos,1.0);\n" +
            "}\n";
    private static final String D_FRAG = "#version 120\n" +
            "void main(){}\n";

    // Pasada 2: Phong + PCF 3×3
    private static final String M_VERT = "#version 120\n" +
            "attribute vec3 aPos, aNorm, aCol;\n" +
            "uniform mat4 uModel, uView, uProj, uLSM;\n" +
            "varying vec3 vPos, vNorm, vCol;\n" +
            "varying vec4 vLS;\n" +
            "void main(){\n" +
            "  vec4 w = uModel * vec4(aPos,1.0);\n" +
            "  vPos  = w.xyz;\n" +
            "  vNorm = mat3(uModel)*aNorm;\n" +
            "  vCol  = aCol;\n" +
            "  vLS   = uLSM * w;\n" +
            "  gl_Position = uProj*uView*w;\n" +
            "}\n";

    private static final String M_FRAG = "#version 120\n" +
            "varying vec3 vPos, vNorm, vCol;\n" +
            "varying vec4 vLS;\n" +
            "uniform vec3      uLP, uCP;\n" +
            "uniform sampler2D uSM;\n" +
            "uniform sampler2D uTex;\n" +
            "uniform float     uEmit;\n" +
            "\n" +
            "float shadowFactor(){\n" +
            "  vec3 p = vLS.xyz / vLS.w;\n" +
            "  p = p * 0.5 + 0.5;\n" +
            "  if(p.x<0.0||p.x>1.0||p.y<0.0||p.y>1.0||p.z>1.0) return 0.0;\n" +
            "  float depth = p.z;\n" +
            "  vec3  n     = normalize(vNorm);\n" +
            "  vec3  ld    = normalize(uLP - vPos);\n" +
            "  float cosA  = max(dot(n, ld), 0.0);\n" +
            "  float bias  = mix(0.0004, 0.00005, cosA);\n" +
            "  float t     = 1.0 / float(" + SM + ");\n" +
            "  float s = 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2(-t,-t)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( 0,-t)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( t,-t)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2(-t, 0)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( 0, 0)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( t, 0)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2(-t, t)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( 0, t)).r) ? 1.0 : 0.0;\n" +
            "  s += (depth-bias > texture2D(uSM, p.xy+vec2( t, t)).r) ? 1.0 : 0.0;\n" +
            "  return s / 9.0;\n" +
            "}\n" +
            "\n" +
            "void main(){\n" +
            "  if(uEmit > 0.5){ gl_FragColor = vec4(vCol,1.0); return; }\n" +
            "  vec3 baseCol = vCol;\n" +
            "  if(uEmit < -0.5) {\n" +
            "      baseCol = texture2D(uTex, vPos.xz * 0.25).rgb;\n" +
            "  }\n" +
            "  vec3 n    = normalize(vNorm);\n" +
            "  vec3 ld   = normalize(uLP - vPos);\n" +
            "  vec3 vd   = normalize(uCP - vPos);\n" +
            "  vec3 h    = normalize(ld + vd);\n" +
            "  float amb  = 0.28;\n" +
            "  float diff = max(dot(n, ld), 0.0);\n" +
            "  float spec = pow(max(dot(n,h),0.0), 48.0) * 0.3;\n" +
            "  float shad = shadowFactor();\n" +
            "  vec3 color = (amb + (1.0-shad)*(diff+spec)) * vec3(1.0,0.95,0.85) * baseCol;\n" +
            "  // Niebla marina\n" +
            "  float dist = length(uCP - vPos);\n" +
            "  float fog  = clamp((350.0-dist)/250.0, 0.0, 1.0);\n" +
            "  vec3 fogC  = vec3(0.53,0.81,0.98);\n" +
            "  gl_FragColor = vec4(mix(fogC, color, fog), 1.0);\n" +
            "}\n";

    private int pDepth, pMain, fbo, smTex;
    private int physicalWidth = 800;
    private int physicalHeight = 600;
    private Texture texturaAdoquin;

  @Override
public void mouseWheelMoved(MouseWheelEvent e) {

    fov += e.getWheelRotation() * 2f;

    if (fov < 20f) fov = 20f;
    if (fov > 120f) fov = 120f;
}
    // Objetos de escena
    record SceneObj(int[] mesh, Matrix4f model, float emit) {
    }

    final List<SceneObj> scene = new ArrayList<>();
    public SceneObj playerObj = null;

    // Cámara
    private float cx = -15, cy = 10f, cz = 5;
    private float yaw = 0, pitch = -12;
    private float fov = 70f;
    private final Set<Integer> keys = Collections.synchronizedSet(new HashSet<>());
    private Robot robot;
    private volatile boolean rMoving = false;
    private long lastNs = System.nanoTime();

    // Luz solar fija
    private final float lx = 80f, ly = 160f, lz = -60f;

    public VeracruzDemo() {
        super(mkCaps());
        addGLEventListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), "blank"));
        try {
            robot = new Robot();
            robot.setAutoDelay(0);
        } catch (AWTException e) {
            System.err.println("Robot: " + e);
        }
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private static GLCapabilities mkCaps() {
        GLCapabilities c = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        c.setDepthBits(24);
        return c;
    }

    @Override
    public void init(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.53f, 0.81f, 0.98f, 1f);

        pDepth = mkProg(gl, D_VERT, D_FRAG, new int[] { 0 }, new String[] { "aPos" });
        pMain = mkProg(gl, M_VERT, M_FRAG, new int[] { 0, 1, 2 }, new String[] { "aPos", "aNorm", "aCol" });

        // Cargar textura de adoquín
        try {
            BufferedImage image = ImageIO.read(new File("imagenes/adoquin.png"));
            texturaAdoquin = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false);
            texturaAdoquin.bind(gl);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        } catch (Exception e) {
            System.out.println("No se pudo cargar textura: imagenes/adoquin.jpg");
        }

        SceneBuilder.build(gl, this);
        buildFBO(gl);
    }

    // FBO shadow map
    private void buildFBO(GL2 gl) {
        int[] t = { 0 };
        gl.glGenTextures(1, t, 0);
        smTex = t[0];
        gl.glBindTexture(GL.GL_TEXTURE_2D, smTex);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT,
                SM, SM, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, null);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        int[] f = { 0 };
        gl.glGenFramebuffers(1, f, 0);
        fbo = f[0];
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fbo);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
                GL.GL_TEXTURE_2D, smTex, 0);
        gl.glDrawBuffer(GL2.GL_NONE);
        gl.glReadBuffer(GL2.GL_NONE);
        int st = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        if (st != GL2.GL_FRAMEBUFFER_COMPLETE)
            System.err.println("FBO incompleto: 0x" + Integer.toHexString(st));
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void display(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();

        long now = System.nanoTime();
        float dt = Math.min((now - lastNs) / 1e9f, 0.1f);
        lastNs = now;
        processKeys(dt);

        if (playerObj != null) {
            double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
            float fx = (float) (Math.cos(yr) * Math.cos(pr));
            float fy = (float) Math.sin(pr);
            float fz = (float) (Math.sin(yr) * Math.cos(pr));
            
            // Posicionar el helicóptero frente a la cámara
            float hx = cx + fx * 15f;
            float hy = cy + fy * 15f - 4f;
            float hz = cz + fz * 15f;

            // Ajustar su rotación para mantener la consistencia
            playerObj.model().identity()
                .translate(hx, hy, hz)
                .rotateY((float) Math.toRadians(-yaw + 90f)) // Mirar en la misma dirección que la cámara
                .rotateX((float) Math.toRadians(pitch - 90f)) // Inclinarse con la cámara y corregir orientación
                .scale(0.009f);
        }

    // Matriz espacio de luz
        Vector3f lDir = new Vector3f(0.45f, 0.35f, -0.82f).normalize();
        Vector3f center = new Vector3f(0f, 0f, 30f); // Desplazado hacia el mar para cubrir el muelle
        Vector3f sunPos = new Vector3f(center).add(new Vector3f(lDir).mul(400f));

        Matrix4f lightView = new Matrix4f().lookAt(sunPos, center, new Vector3f(0f, 0f, 1f));
        Matrix4f lightProj = new Matrix4f().ortho(-350f, 350f, -350f, 350f, 1f, 800f);
        Matrix4f LSM = new Matrix4f(lightProj).mul(lightView);

        // Pasada shadow map
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fbo);
        gl.glViewport(0, 0, SM, SM);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glColorMask(false, false, false, false);

        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(2f, 4f);

        gl.glUseProgram(pDepth);
        setMat4(gl, pDepth, "uLSM", LSM);

        for (SceneObj o : scene) {
            if (o.emit > 0.5f)
                continue; // No poner sombra al Sol
            setMat4(gl, pDepth, "uModel", o.model);
            drawMesh(gl, o.mesh, false);
        }

        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glColorMask(true, true, true, true);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

        // Pasada render principal
        gl.glViewport(0, 0, physicalWidth, physicalHeight);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        float asp = physicalWidth > 0 ? (float) physicalWidth / physicalHeight : 16f / 9f;
        Matrix4f proj = new Matrix4f().perspective(
    (float) Math.toRadians(fov),
    asp,
    0.1f,
    500f
);
        Matrix4f view = camView();

        gl.glUseProgram(pMain);
        setMat4(gl, pMain, "uView", view);
        setMat4(gl, pMain, "uProj", proj);
        setMat4(gl, pMain, "uLSM", LSM);
        setVec3(gl, pMain, "uLP", sunPos.x, sunPos.y, sunPos.z);
        setVec3(gl, pMain, "uCP", cx, cy, cz);
        setInt(gl, pMain, "uSM", 0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, smTex);

        gl.glActiveTexture(GL.GL_TEXTURE1);
        if (texturaAdoquin != null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, texturaAdoquin.getTextureObject());
        } else {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
        setInt(gl, pMain, "uTex", 1);

        for (SceneObj o : scene) {
            setFloat(gl, pMain, "uEmit", o.emit);
            setMat4(gl, pMain, "uModel", o.model);
            drawMesh(gl, o.mesh, true);
        }
    }

    private void drawMesh(GL2 gl, int[] m, boolean full) {
        int stride = 9 * Float.BYTES;
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, m[0]);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, m[1]);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, stride, 0L);
        if (full) {
            gl.glEnableVertexAttribArray(1);
            gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, stride, 12L);
            gl.glEnableVertexAttribArray(2);
            gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, stride, 24L);
        }
        gl.glDrawElements(GL.GL_TRIANGLES, m[2], GL.GL_UNSIGNED_INT, 0L);

        gl.glDisableVertexAttribArray(0);
        if (full) {
            gl.glDisableVertexAttribArray(1);
            gl.glDisableVertexAttribArray(2);
        }
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    int[] upload(GL2 gl, float[] v, int[] idx) {
        int[] m = new int[3];
        int[] ids = new int[2];
        gl.glGenBuffers(2, ids, 0);
        m[0] = ids[0];
        m[1] = ids[1];
        m[2] = idx.length;

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, m[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) v.length * Float.BYTES,
                FloatBuffer.wrap(v), GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, m[1]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, (long) idx.length * Integer.BYTES,
                IntBuffer.wrap(idx), GL.GL_STATIC_DRAW);
        return m;
    }

    void add(int[] mesh, Matrix4f model) {
        scene.add(new SceneObj(mesh, model, 0f));
    }

    void addEmit(int[] mesh, Matrix4f model) {
        scene.add(new SceneObj(mesh, model, 1f));
    }

    public void addStreet(int[] mesh, Matrix4f model) {
        scene.add(new SceneObj(mesh, model, -1f));
    }

    // Shaders
    private int mkProg(GL2 gl, String vs, String fs, int[] locs, String[] names) {
        int v = mkShader(gl, GL2.GL_VERTEX_SHADER, vs);
        int f = mkShader(gl, GL2.GL_FRAGMENT_SHADER, fs);
        int p = gl.glCreateProgram();
        for (int i = 0; i < locs.length; i++)
            gl.glBindAttribLocation(p, locs[i], names[i]);
        gl.glAttachShader(p, v);
        gl.glAttachShader(p, f);
        gl.glLinkProgram(p);
        int[] ok = { 0 };
        gl.glGetProgramiv(p, GL2.GL_LINK_STATUS, ok, 0);
        if (ok[0] == 0) {
            byte[] log = new byte[2048];
            gl.glGetProgramInfoLog(p, log.length, null, 0, log, 0);
            throw new RuntimeException("Link:\n" + new String(log).trim());
        }
        gl.glDeleteShader(v);
        gl.glDeleteShader(f);
        return p;
    }

    private int mkShader(GL2 gl, int type, String src) {
        int id = gl.glCreateShader(type);
        gl.glShaderSource(id, 1, new String[] { src }, null, 0);
        gl.glCompileShader(id);
        int[] ok = { 0 };
        gl.glGetShaderiv(id, GL2.GL_COMPILE_STATUS, ok, 0);
        if (ok[0] == 0) {
            byte[] log = new byte[2048];
            gl.glGetShaderInfoLog(id, log.length, null, 0, log, 0);
            throw new RuntimeException("Compile:\n" + new String(log).trim());
        }
        return id;
    }

    // Uniforms
    private void setMat4(GL2 gl, int p, String n, Matrix4f m) {
        int l = gl.glGetUniformLocation(p, n);
        if (l < 0)
            return;
        float[] a = new float[16];
        m.get(a);
        gl.glUniformMatrix4fv(l, 1, false, a, 0);
    }

    private void setVec3(GL2 gl, int p, String n, float x, float y, float z) {
        int l = gl.glGetUniformLocation(p, n);
        if (l >= 0)
            gl.glUniform3f(l, x, y, z);
    }

    private void setFloat(GL2 gl, int p, String n, float v) {
        int l = gl.glGetUniformLocation(p, n);
        if (l >= 0)
            gl.glUniform1f(l, v);
    }

    private void setInt(GL2 gl, int p, String n, int v) {
        int l = gl.glGetUniformLocation(p, n);
        if (l >= 0)
            gl.glUniform1i(l, v);
    }

    // Cámara
    private Matrix4f camView() {
        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        float fx = (float) (Math.cos(yr) * Math.cos(pr));
        float fy = (float) Math.sin(pr);
        float fz = (float) (Math.sin(yr) * Math.cos(pr));
        return new Matrix4f().lookAt(
                new Vector3f(cx, cy, cz),
                new Vector3f(cx + fx, cy + fy, cz + fz),
                new Vector3f(0f, 1f, 0f));
    }

    private void processKeys(float dt) {
        float spd = 10f * dt;
        if (keys.contains(KeyEvent.VK_SHIFT))
            spd *= 3f;
        double yr = Math.toRadians(yaw);
        float fx = (float) Math.cos(yr), fz = (float) Math.sin(yr);
        if (keys.contains(KeyEvent.VK_W)) {
            cx += fx * spd;
            cz += fz * spd;
        }
        if (keys.contains(KeyEvent.VK_S)) {
            cx -= fx * spd;
            cz -= fz * spd;
        }
        if (keys.contains(KeyEvent.VK_A)) {
            cx += fz * spd;
            cz -= fx * spd;
        }
        if (keys.contains(KeyEvent.VK_D)) {
            cx -= fz * spd;
            cz += fx * spd;
        }
        if (keys.contains(KeyEvent.VK_Q))
            cy += spd;
        if (keys.contains(KeyEvent.VK_E))
            cy -= spd;
        if (cy < 0.3f)
            cy = 0.3f;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        System.exit(0);

    if (e.getKeyCode() == KeyEvent.VK_ADD)
        fov -= 2f;

    if (e.getKeyCode() == KeyEvent.VK_SUBTRACT)
        fov += 2f;

    if (fov < 20f) fov = 20f;
    if (fov > 120f) fov = 120f;

    keys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (rMoving) {
            rMoving = false;
            return;
        }
        if (robot == null || !isShowing())
            return;
        Point loc;
        try {
            loc = getLocationOnScreen();
        } catch (IllegalComponentStateException ex) {
            return;
        }
        int cx2 = loc.x + getWidth() / 2, cy2 = loc.y + getHeight() / 2;
        float dx = e.getXOnScreen() - cx2;
        float dy = -(e.getYOnScreen() - cy2);
        if (Math.abs(dx) > 0.1f || Math.abs(dy) > 0.1f) {
            yaw += dx * 0.12f;
            pitch = Math.max(-89f, Math.min(89f, pitch + dy * 0.12f));
        }
        rMoving = true;
        robot.mouseMove(cx2, cy2);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
        this.physicalWidth = w;
        this.physicalHeight = h;
        d.getGL().getGL2().glViewport(0, 0, w, h);
    }

    @Override
    public void dispose(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();
        gl.glDeleteProgram(pDepth);
        gl.glDeleteProgram(pMain);
        gl.glDeleteFramebuffers(1, new int[] { fbo }, 0);
        gl.glDeleteTextures(1, new int[] { smTex }, 0);
    }
}