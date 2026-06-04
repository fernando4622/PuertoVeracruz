package veracruz;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import static veracruz.Geo.*;

/**
 *
 * @author ferna
 */
public final class SceneBuilder {

  private SceneBuilder() {
  }

  public static void build(GL2 gl, VeracruzDemo d) {
    buildSol(gl, d);
    buildNubes(gl, d);
    buildMar(gl, d);
    buildSuelo(gl, d);
    buildMalecon(gl, d);
    buildMonumento(gl, d);
    buildMuelleMar(gl, d);
    buildTorreReloj(gl, d);
    buildTorrePemex(gl, d);
    buildEdificiosColoniales(gl, d);
    buildParque(gl, d);
    buildRemolcadores(gl, d);
    buildGrua(gl, d);
    buildFondo(gl, d);
    buildCerezosAvenida(gl, d);
    buildObjModel(gl, d);
  }

  // Sol
  private static void buildSol(GL2 gl, VeracruzDemo d) {
    float dist = 280f, sx = 0.45f * dist, sy = 0.35f * dist, sz = -0.82f * dist;
    d.addEmit(sphere(gl, d, 18f, 32, 32, 1f, 0.97f, 0.8f), new Matrix4f().translate(sx, sy, sz));
    d.addEmit(sphere(gl, d, 30f, 24, 24, 1f, 0.98f, 0.85f), new Matrix4f().translate(sx, sy + 2, sz));
  }

  // Nubes
  private static void buildNubes(GL2 gl, VeracruzDemo d) {
    float[][] g = { { -180, 85, 60 }, { -60, 95, 40 }, { 80, 90, 55 }, { 200, 80, 70 }, { -120, 75, 120 } };
    for (float[] p : g) {
      buildNube(gl, d, p[0], p[1], p[2]);
    }
  }

  private static void buildNube(GL2 gl, VeracruzDemo d, float x, float y, float z) {
    addSphere(gl, d, x, y - 4, z, 22, 8, 16, .78f, .80f, .84f);
    addSphere(gl, d, x, y, z, 26, 10, 18, .96f, .97f, .98f);
    addSphere(gl, d, x + 12, y + 8, z + 5, 18, 8, 14, .96f, .97f, .98f);
    addSphere(gl, d, x - 10, y + 10, z - 8, 14, 8, 12, .96f, .97f, .98f);
  }

  // Mar
  private static void buildMar(GL2 gl, VeracruzDemo d) {
    d.add(ground(gl, d, 2000, 1200, .05f, .28f, .62f), new Matrix4f().translate(0, -3.5f, 300));
    d.add(ground(gl, d, 1200, 600, .08f, .38f, .72f), new Matrix4f().translate(0, -1.8f, 150));
    d.add(ground(gl, d, 800, 200, .18f, .55f, .82f), new Matrix4f().translate(0, -0.3f, 50));
  }

  // Suelo
  private static void buildSuelo(GL2 gl, VeracruzDemo d) {
    d.add(ground(gl, d, 360, 200, .68f, .66f, .62f), new Matrix4f().translate(0, -0.01f, -85));

    // Calles con adoquines con textura
    addCalleVertical(gl, d, -44, -100, 170, 14); // Calle vertical izquierda
    addCalleVertical(gl, d, 44, -100f, 170, 14); // Calle vertical derecha (junto Torre Pemex)
    addCalleVertical(gl, d, -166, -100f, 170, 14);
    addCalleHorizontal(gl, d, 5, -90, 350f, 16); // Calle trasera
  }

  // Malecón
  private static void buildMalecon(GL2 gl, VeracruzDemo d) {
    // Concreto
    addBox(gl, d, 0, .5f, 15, 360, 1, 60, .58f, .57f, .55f);
    addBox(gl, d, 150, .5f, -70, 60, 1, 230, .58f, .57f, .55f);

    // Muro frontal con franja rojo/blanco
    addBox(gl, d, 0, 1.75f, 44.5f, 362, 3.5f, 1.2f, .51f, .50f, .48f);
    addBox(gl, d, 0, 3.75f, 44.5f, 362, .5f, 1.3f, .82f, .15f, .10f);

    // Pilonas alternadas en el borde frontal
    for (float bx = -176; bx <= 176; bx += 8) {
      boolean red = ((int) (bx / 8) % 2 == 0);
      float cr = red ? .82f : .95f, cg = red ? .15f : .95f, cb = red ? .10f : .95f;
      addCyl(gl, d, bx, 1.6f, 45, .22f, 1.2f, 8, cr, cg, cb);
    }

    // Lámparas del malecón
    for (float px = -175; px <= 175; px += 25) {
      addCyl(gl, d, px, 2, 43, .9f, 6, 8, .30f, .32f, .34f);
    }

    // Marcas blancas en el concreto
    float lw = .28f; // grosor de línea
    for (float mx = -162; mx <= 130; mx += 18) {
      float rW = 8f, rZ = 30f, rH = 11f, ry = 1.01f;
      addBox(gl, d, mx, ry, rZ - rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, mx, ry, rZ + rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, mx - rW * .5f, ry, rZ, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, mx + rW * .5f, ry, rZ, lw, .02f, rH, .92f, .92f, .90f);
    }

    for (float mx = -162; mx <= 130; mx += 18) {
      float rW = 8f, rZ = 0f, rH = 11f, ry = 1.01f;
      addBox(gl, d, mx, ry, rZ - rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, mx, ry, rZ + rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, mx - rW * .5f, ry, rZ, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, mx + rW * .5f, ry, rZ, lw, .02f, rH, .92f, .92f, .90f);
    }

    for (float mz = -170; mz <= -20; mz += 18) {
      float rW = 11f, rH = 8f, rx = 165f, ry = 1.01f;

      addBox(gl, d, rx - rW * .5f, ry, mz, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, rx + rW * .5f, ry, mz, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, rx, ry, mz - rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, rx, ry, mz + rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
    }

    for (float mz = -170; mz <= -20; mz += 18) {
      float rW = 11f, rH = 8f, rx = 135f, ry = 1.01f;

      addBox(gl, d, rx - rW * .5f, ry, mz, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, rx + rW * .5f, ry, mz, lw, .02f, rH, .92f, .92f, .90f);
      addBox(gl, d, rx, ry, mz - rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
      addBox(gl, d, rx, ry, mz + rH * .5f, rW, .02f, lw, .92f, .92f, .90f);
    }
    // Línea central amarilla que divide la calzada
    addBox(gl, d, -15, 1.01f, 15, 330, .02f, .32f, .88f, .82f, .12f);
    addBox(gl, d, 150, 1.01f, -85, .32f, .02f, 200, .88f, .82f, .12f);
  }

  // Palacio con el busto de Carranza
  private static void buildTorreReloj(GL2 gl, VeracruzDemo d) {
    float tx = 15f, tz = -125;
    float angleDeg = -90f;
    float angleRad = (float) Math.toRadians(angleDeg);

    
    float pw = 36f, pd = 14f, ph = 8.5f;
    addBoxR(gl, d, tx, tz, tx, ph / 2f, tz, pw, ph, pd, .93f, .91f, .87f, angleRad);

    addBoxR(gl, d, tx, tz, tx, 4.25f, tz, pw + 0.6f, 0.4f, pd + 0.6f, .75f, .42f, .25f, angleRad);

    addBoxR(gl, d, tx, tz, tx, ph + 0.3f, tz, pw + 0.8f, 0.6f, pd + 0.8f, .75f, .42f, .25f, angleRad);

    addBoxR(gl, d, tx, tz, tx - pw / 2f + 3.5f, (ph + 1.5f) / 2f, tz, 7f, ph + 1.5f, pd + 0.4f, .91f, .89f, .85f, angleRad);
    addSphereR(gl, d, tx, tz, tx - pw / 2f + 3.5f, ph + 2.0f, tz, 1.2f, 8, 10, .78f, .73f, .65f, angleRad);

    addBoxR(gl, d, tx, tz, tx + pw / 2f - 3.5f, (ph + 1.5f) / 2f, tz, 7f, ph + 1.5f, pd + 0.4f, .91f, .89f, .85f, angleRad);
    addSphereR(gl, d, tx, tz, tx + pw / 2f - 3.5f, ph + 2.0f, tz, 1.2f, 8, 10, .78f, .73f, .65f, angleRad);

    float cr = .22f, cg = .35f, cb = .55f;
    for (int c = 0; c < 3; c++) {
      float vx = tx - 12f + c * 3.5f;
      addBoxR(gl, d, tx, tz, vx, 2.1f, tz - pd / 2f - 0.2f, 1.2f, 2.2f, 0.6f, cr, cg, cb, angleRad);
      addBoxR(gl, d, tx, tz, vx, 6.35f, tz - pd / 2f - 0.2f, 1.2f, 2.2f, 0.6f, cr, cg, cb, angleRad);
    }
    for (int c = 0; c < 3; c++) {
      float vx = tx + 5f + c * 3.5f;
      addBoxR(gl, d, tx, tz, vx, 2.1f, tz - pd / 2f - 0.2f, 1.2f, 2.2f, 0.6f, cr, cg, cb, angleRad);
      addBoxR(gl, d, tx, tz, vx, 6.35f, tz - pd / 2f - 0.2f, 1.2f, 2.2f, 0.6f, cr, cg, cb, angleRad);
    }

    float tw = 6.2f, td = 6.2f, th = 26f;
    addBoxR(gl, d, tx, tz, tx, th / 2f + 4f, tz, tw, th, td, .93f, .91f, .87f, angleRad);

    for (int p = 0; p < 3; p++) {
      float vy = 11f + p * 5.5f;
      addBoxR(gl, d, tx, tz, tx, vy, tz - td / 2f - 0.2f, 1.2f, 2.6f, 0.8f, cr, cg, cb, angleRad);
    }

    float relojY = 22f;
    addBoxR(gl, d, tx, tz, tx, relojY + 2.5f, tz - td / 2f - 0.2f, 2.5f, 2.5f, 0.8f, .85f, .88f, .92f, angleRad);

    for (int p = 0; p < 4; p++) {
      float oy = 7f + p * 4.5f;
      addBoxR(gl, d, tx, tz, tx, oy, tz - td / 2f - 0.2f, tw + 0.3f, 0.35f, 0.8f, .82f, .52f, .28f, angleRad);
      addBoxR(gl, d, tx, tz, tx, oy, tz + td / 2f + 0.2f, tw + 0.3f, 0.35f, 0.8f, .82f, .52f, .28f, angleRad);
    }

    addBoxR(gl, d, tx, tz, tx, th + 3.5f, tz, tw + 1.2f, 0.8f, td + 1.2f, .82f, .68f, .48f, angleRad);

    float cupolaY = th + 4.2f;
    addCylR(gl, d, tx, tz, tx, cupolaY + 0.6f, tz, 2.4f, 1.2f, 14, .78f, .73f, .65f, angleRad);
    addSphereR(gl, d, tx, tz, tx, cupolaY + 2.8f, tz, 2.1f, 10, 16, .93f, .91f, .87f, angleRad);
    addCylR(gl, d, tx, tz, tx, cupolaY + 5.8f, tz, 0.8f, 2.0f, 10, .93f, .91f, .87f, angleRad);
    addSphereR(gl, d, tx, tz, tx, cupolaY + 7.4f, tz, 0.9f, 8, 12, .78f, .73f, .65f, angleRad);
    addCylR(gl, d, tx, tz, tx, cupolaY + 9.0f, tz, 0.1f, 2.0f, 6, .62f, .64f, .66f, angleRad);

    float stX = tx, stZ = tz - pd / 2f - 4.5f;
    addBoxR(gl, d, tx, tz, stX, 2.1f, stZ, 3.2f, 4.2f, 3.2f, .14f, .14f, .14f, angleRad);
    // Estatua de bronce
    float[] bronze = { .45f, .35f, .22f };
    addBoxR(gl, d, tx, tz, stX, 5.8f, stZ, 1.2f, 3.2f, 1.2f, bronze[0], bronze[1], bronze[2], angleRad); // Cuerpo
    addSphereR(gl, d, tx, tz, stX, 7.6f, stZ, 0.55f, 8, 8, bronze[0], bronze[1], bronze[2], angleRad); // Cabeza

    // Jardín
    addBox(gl, d, 27.0f, 0.05f, tz, 8.5f, 0.15f, 38.0f, .93f, .91f, .87f); // Borde blanco
    addBox(gl, d, 27.0f, 0.12f, tz, 8.0f, 0.15f, 37.0f, .32f, .58f, .30f); // Pasto verde
  }

  // Torre Pemex
  private static void buildTorrePemex(GL2 gl, VeracruzDemo d) {
    float tx = 80, tz = -30;
    float tw = 26, td = 18, th = 58;
    float cw = .92f, cd = .90f, cb = .86f; // blanco hueso
    float gw = .10f, gd = .12f, gb = .18f; // vidrio muy oscuro
    float pw = .91f, pd = .89f, pb = .85f; // pilares blancos

    // Base
    addBox(gl, d, tx, 3.5f, tz, tw + 14, 7, td + 14, .40f, .40f, .42f);
    addBox(gl, d, tx, 7.5f, tz, tw + 8, 1.2f, td + 8, .50f, .50f, .52f);
    addBox(gl, d, tx, 3.5f, tz - td / 2 - 7.5f, 9, 5.5f, .5f, .15f, .15f, .17f);

    // Estructura
    addBox(gl, d, tx, 8 + th / 2, tz, tw, th, td, cw, cd, cb);

    float pilW = 5.5f, pilD = 4.5f, pilH = th + 2;
    float ox = tw / 2 + pilW / 2 - 2f;
    float oz = td / 2 + pilD / 2 - 2f;
    float[][] corners = {
        { tx - ox, tz - oz }, { tx + ox, tz - oz },
        { tx - ox, tz + oz }, { tx + ox, tz + oz }
    };
    for (float[] c : corners)
      addBox(gl, d, c[0], 8 + pilH / 2, c[1], pilW, pilH, pilD, pw, pd, pb);

    addBox(gl, d, tx, 8 + th / 2, tz - td / 2 - .1f, 2f, th, .5f, pw, pd, pb);
    addBox(gl, d, tx, 8 + th / 2, tz + td / 2 + .1f, 2f, th, .5f, pw, pd, pb);

    float glassH = th - 1f;
    float glassY = 8 + 0.5f + glassH / 2;
    float glassW = (tw / 2 - pilW / 2 - 1f) * 0.88f;

    addBox(gl, d, tx - tw / 4, glassY, tz - td / 2 - .22f, glassW, glassH, .45f, gw, gd, gb);
    addBox(gl, d, tx + tw / 4, glassY, tz - td / 2 - .22f, glassW, glassH, .45f, gw, gd, gb);
    addBox(gl, d, tx - tw / 4, glassY, tz + td / 2 + .22f, glassW, glassH, .45f, gw, gd, gb);
    addBox(gl, d, tx + tw / 4, glassY, tz + td / 2 + .22f, glassW, glassH, .45f, gw, gd, gb);

    float sideGlass = td - pilD - 1f;
    addBox(gl, d, tx - tw / 2 - .22f, glassY, tz, .45f, glassH, sideGlass, gw, gd, gb);
    addBox(gl, d, tx + tw / 2 + .22f, glassY, tz, .45f, glassH, sideGlass, gw, gd, gb);

    int pisos = 22;
    float flH = th / pisos;
    for (int b = 1; b < pisos; b++) {
      float by = 8 + b * flH;
      addBox(gl, d, tx, by, tz - td / 2 - .24f, tw - 4, .28f, .48f, .84f, .82f, .78f);
      addBox(gl, d, tx, by, tz + td / 2 + .24f, tw - 4, .28f, .48f, .84f, .82f, .78f);
      addBox(gl, d, tx - tw / 2 - .24f, by, tz, .48f, .28f, td - 5, .84f, .82f, .78f);
      addBox(gl, d, tx + tw / 2 + .24f, by, tz, .48f, .28f, td - 5, .84f, .82f, .78f);
    }

    float top = 8 + th;
    float roofW = tw + 16, roofD = td + 16;

    addBox(gl, d, tx, top + .3f, tz, roofW, .5f, roofD, .62f, .60f, .58f);
    addBox(gl, d, tx, top + 1.2f, tz, roofW, 1.8f, roofD, .88f, .86f, .82f);
    addBox(gl, d, tx, top + 2.2f, tz, roofW + 1f, .5f, roofD + 1f, .70f, .68f, .64f);

    int ng = 6;
    for (int g = 0; g <= ng; g++) {
      float gx = tx - roofW / 2 + g * roofW / ng;
      addBox(gl, d, gx, top + 1.2f, tz, .55f, 2.6f, roofD, .70f, .68f, .64f);
    }
    for (int g = 0; g <= ng; g++) {
      float gz = tz - roofD / 2 + g * roofD / ng;
      addBox(gl, d, tx, top + 1.2f, gz, roofW, 2.6f, .55f, .70f, .68f, .64f);
    }

    addBox(gl, d, tx, top + 4f, tz, tw + 4, 2.5f, td + 4, cw, cd, cb);
    addBox(gl, d, tx, top + 5.8f, tz, tw + 12, 1.0f, td + 12, .85f, .83f, .79f);
    addBox(gl, d, tx, top + 6.4f, tz, tw + 13, .4f, td + 13, .70f, .68f, .64f);

    addCyl(gl, d, tx, top + 8f, tz, 3.0f, 2.5f, 12, .78f, .76f, .72f);
    addSphere(gl, d, tx, top + 10f, tz, 2.2f, 8, 12, .88f, .86f, .82f);
    addCyl(gl, d, tx, top + 14f, tz, .22f, 7.0f, 6, .50f, .50f, .52f);
    addSphere(gl, d, tx, top + 17f, tz, .5f, 6, 8, .60f, .60f, .62f);

    // Jardines
    addBox(gl, d, 108, 0.1f, -48, 20, 0.2f, 60, .32f, .58f, .30f);
    addBox(gl, d, 57, 0.1f, -48, 8, 0.2f, 60, .32f, .58f, .30f);
    
    // Jardín de la torre Pemex
    buildJardinTorrePemex(gl, d);
  }

  private static void buildEdificiosColoniales(GL2 gl, VeracruzDemo d) {
    buildColonial(gl, d, -68, -35, 26, 16, 3, -90f); // Edificio en primer plano a la izquierda plaza
    buildColonial(gl, d, -68, -65, 26, 16, 4, -90f); // Edificio en el medio a la izquierda plaza
    buildColonial(gl, d, -73, -118, 26, 26, 3, -90);
    buildColonial(gl, d, -68, -150, 30, 18, 5, -90);

    buildColonial(gl, d, -18, -115, 30, 18, 4, -180); // Atrás del palacio
    buildColonial(gl, d, 72, -115, 24, 16, 3, 180); // Frente Torre Pemex

    buildColonial(gl, d, -150, -65, 26, 16, 4, 90f); // Fondo
    buildColonial(gl, d, -145, -35, 26, 26, 3, 90);
    buildColonial(gl, d, -150, -65, 26, 16, 4, -90f); // Fondo repetido para que se vean ambos lados con ventanas
    buildColonial(gl, d, -145, -35, 26, 26, 3, -90);

    buildColonial(gl, d, 65, -150, 24, 16, 8, 90); // ContraEsq Palacio al frente
    buildColonial(gl, d, 25, -165, 24, 16, 7, -90);

    buildColonial(gl, d, 100, -165, 30, 20, 4, -90);
    buildColonial(gl, d, 100, -125, 40, 20, 7, -90);

    buildColonial(gl, d, -25, -150, 26, 16, 9, 90f);

  }

  private static void buildColonial(GL2 gl, VeracruzDemo d, float ex, float ez, float ew, float ed, int pisos) {
    float ph = pisos * 3.8f;
    float r = (pisos % 3 == 0) ? .72f : .93f, g = (pisos % 3 == 0) ? .38f : .91f,
        b = (pisos % 3 == 0) ? .25f : .87f;
    addBox(gl, d, ex, ph / 2, ez, ew, ph, ed, r, g, b);
    addBox(gl, d, ex, ph + .4f, ez, ew + .6f, .8f, ed + .6f, .75f, .42f, .25f);
    int cols = Math.max(2, (int) (ew / 4));
    for (int p = 0; p < pisos; p++) {
      float flH = ph / pisos, vy = p * flH + flH * .5f;
      for (int c = 0; c < cols; c++) {
        float vx = ex - ew / 2 + ew / (cols + 1f) * (c + 1);
        addBox(gl, d, vx, vy, ez - ed / 2 - .18f, 1.6f, 2.2f, .35f, .25f, .40f, .62f);
      }
    }
  }

  private static void buildParque(GL2 gl, VeracruzDemo d) {
    d.add(ground(gl, d, 70, 60, .88f, .88f, .85f), new Matrix4f().translate(0f, 0.06f, -46));

    // Fuente en el centro
    addCyl(gl, d, 0f, .4f, -46, 8, .8f, 20, .78f, .73f, .65f);
    addCyl(gl, d, 0f, .9f, -46, 6, .5f, 16, .15f, .38f, .65f);
    addCyl(gl, d, 0f, 2.8f, -46, .6f, 3.5f, 10, .93f, .91f, .87f);
    addSphere(gl, d, 0f, 5, -46, 1.2f, 8, 10, .15f, .38f, .65f);

    // Obelisco en la izquierda
    addBox(gl, d, -22, 1.5f, -46, 4, 3, 4, .78f, .73f, .65f);
    addBox(gl, d, -22, 10, -46, 2, 14, 2, .93f, .91f, .87f);
    addSphere(gl, d, -22, 17.5f, -46, .5f, 6, 8, .85f, .70f, .15f);

    // Jardines
    addBox(gl, d, -25, .1f, -28, 14, .2f, 20, .32f, .58f, .30f);
    addBox(gl, d, -25, .1f, -64, 14, .2f, 20, .32f, .58f, .30f);
    addBox(gl, d, 25, .1f, -28, 14, .2f, 20, .32f, .58f, .30f);
    addBox(gl, d, 25, .1f, -64, 14, .2f, 20, .32f, .58f, .30f);

    // Arboles
    for (float z = -70; z <= -15; z += 20) {
      buildArbol(gl, d, -52, z, 7f);
      buildArbol(gl, d, -36, z, 7f);
      buildArbol(gl, d, 36, z, 7f);
      buildArbol(gl, d, 52, z, 7f);
    }

    // Cerezos 
    buildCerezo(gl, d, -26, -28, 6.5f);
    buildCerezo(gl, d, -26, -64, 7f);
    buildCerezo(gl, d, 26, -28, 6f);
    buildCerezo(gl, d, 26, -64, 6.5f);
  }

  private static void buildCerezosAvenida(GL2 gl, VeracruzDemo d) {
    for (int i = -175; i < 125; i += 20) {
      if ((i >= 32 && i <= 56) || (i >= -56 && i <= -32) || (i >= -178 && i <= -154)) {
        continue;
      }

      if ((i >= 60 && i <= 100)) {
        continue;
      }
      buildCerezo(gl, d, i, -100, 6.5f);
      buildArbol(gl, d, i - 10, -100, 6.5f);
      buildCerezo(gl, d, i, -80, 6.5f);
      buildArbol(gl, d, i - 10, -80, 6.5f);
    }
  }

  private static void buildJardinTorrePemex(GL2 gl, VeracruzDemo d) {
    float jx = 80f;

    // Piso rosado
    addBox(gl, d, jx, 0.02f, -60.0f, 35.0f, 0.04f, 32.0f, 0.85f, 0.65f, 0.62f); // Granito rosa cantera

    float rz = -50f;
    // Pasto circular
    addCyl(gl, d, jx, 0.25f, rz, 5.0f, 0.5f, 16, .32f, .58f, .30f); // Pasto verde
    // Borde blanco circular
    addCyl(gl, d, jx, 0.2f, rz, 5.3f, 0.4f, 16, .93f, .91f, .87f); // Borde exterior
    addCyl(gl, d, jx, 9.0f, rz, 0.15f, 18.0f, 8, .85f, .86f, .88f); // Asta de acero
    addCyl(gl, d, jx, 0.6f, rz, 0.8f, 1.2f, 10, .14f, .14f, .14f); // Base cónica oscura
    addSphere(gl, d, jx, 1.3f, rz, 0.6f, 8, 8, .85f, .70f, .15f); // Remate dorado base

    float oz = -70f;
    addBox(gl, d, jx, 0.15f, oz, 13.0f, 0.3f, 7.0f, .93f, .91f, .87f); // Borde
    addBox(gl, d, jx, 0.25f, oz, 12.0f, 0.5f, 6.0f, .32f, .58f, .30f); // Pasto

    buildArbol(gl, d, jx, oz, 8.5f);

    addBox(gl, d, jx, 1.2f, oz - 4.5f, 9.0f, 2.4f, 1.2f, .75f, .22f, .20f); // Muro de realce rojo

    float ry_line = 0.07f;
    float rw_line = 0.35f;
    float[] redCol = { .78f, .16f, .18f };

    addBox(gl, d, jx - 14.0f, ry_line, -70.0f, rw_line, 0.02f, 10.0f, redCol[0], redCol[1], redCol[2]);
    addBox(gl, d, jx + 14.0f, ry_line, -70.0f, rw_line, 0.02f, 10.0f, redCol[0], redCol[1], redCol[2]);

    addBox(gl, d, jx - 11.5f, ry_line, -65.0f, 5.0f + rw_line, 0.02f, rw_line, redCol[0], redCol[1], redCol[2]);
    addBox(gl, d, jx + 11.5f, ry_line, -65.0f, 5.0f + rw_line, 0.02f, rw_line, redCol[0], redCol[1], redCol[2]);

    addBox(gl, d, jx - 9.0f, ry_line, -58.0f, rw_line, 0.02f, 14.0f, redCol[0], redCol[1], redCol[2]);
    addBox(gl, d, jx + 9.0f, ry_line, -58.0f, rw_line, 0.02f, 14.0f, redCol[0], redCol[1], redCol[2]);

    addBox(gl, d, jx - 7.5f, ry_line, -51.0f, 3.0f + rw_line, 0.02f, rw_line, redCol[0], redCol[1], redCol[2]);
    addBox(gl, d, jx + 7.5f, ry_line, -51.0f, 3.0f + rw_line, 0.02f, rw_line, redCol[0], redCol[1], redCol[2]);

    addBox(gl, d, jx - 6.0f, ry_line, -47.5f, rw_line, 0.02f, 7.0f, redCol[0], redCol[1], redCol[2]);
    addBox(gl, d, jx + 6.0f, ry_line, -47.5f, rw_line, 0.02f, 7.0f, redCol[0], redCol[1], redCol[2]);

    addBox(gl, d, jx, ry_line, -75.0f, 28.0f + rw_line, 0.02f, rw_line, redCol[0], redCol[1], redCol[2]);
  }

  private static void buildMonumento(GL2 gl, VeracruzDemo d) {
    float mx = 0f, mz = -20f;

    addBox(gl, d, mx, .7f, mz, 6.5f, 1.4f, 6.5f, .80f, .78f, .74f);
    addBox(gl, d, mx, 1.6f, mz, 5.0f, 1.0f, 5.0f, .85f, .83f, .79f);
    addBox(gl, d, mx, 2.3f, mz, 3.5f, .6f, 3.5f, .90f, .88f, .84f);

    addBox(gl, d, mx, 15f, mz, 1.7f, 26.0f, 1.7f, .93f, .91f, .87f);

    addBox(gl, d, mx, 27.8f, mz, 2.6f, 1.0f, 2.6f, .88f, .86f, .82f);
    addSphere(gl, d, mx, 29f, mz, 1.0f, 8, 10, .78f, .73f, .65f);

    for (float oy = 4.5f; oy < 28.0f; oy += 5.5f) {
      addBox(gl, d, mx, oy, mz, 2.1f, .18f, 2.1f, .80f, .50f, .26f);
    }

    float[] br = { .38f, .28f, .18f };
    addBox(gl, d, mx, 3.6f, mz - 2.0f, 1.0f, 3.0f, 1.0f, br[0], br[1], br[2]);
    addSphere(gl, d, mx, 5.3f, mz - 2.0f, .52f, 8, 8, br[0], br[1], br[2]);
  }

  private static void buildMuelleMar(GL2 gl, VeracruzDemo d) {
    final float armW = 22f;
    final float lxC = -91f;
    final float rxC = 91f;
    final float z0 = 44f;
    final float z1 = 136f;
    final float zC = (z0 + z1) * 0.5f;
    final float armL = z1 - z0;
    final float half = armW * 0.5f;

    final float sR = .54f, sG = .53f, sB = .51f;
    final float wR = .49f, wG = .48f, wB = .46f;

    addBox(gl, d, lxC, .3f, zC, armW, .65f, armL, sR, sG, sB);
    addBox(gl, d, lxC - half, 1.3f, zC, .85f, 2.6f, armL, wR, wG, wB);
    addBox(gl, d, lxC + half, 1.3f, zC, .85f, 2.6f, armL, wR, wG, wB);
    addBox(gl, d, lxC, 1.3f, z1, armW, 2.6f, .85f, wR, wG, wB);
    addBox(gl, d, lxC - half, 3.0f, zC, .4f, .5f, armL, .82f, .15f, .10f);
    addBox(gl, d, lxC + half, 3.0f, zC, .4f, .5f, armL, .82f, .15f, .10f);

    addBox(gl, d, rxC, .3f, zC, armW, .65f, armL, sR, sG, sB);
    addBox(gl, d, rxC + half, 1.3f, zC, .85f, 2.6f, armL, wR, wG, wB);
    addBox(gl, d, rxC - half, 1.3f, zC, .85f, 2.6f, armL, wR, wG, wB);
    addBox(gl, d, rxC, 1.3f, z1, armW, 2.6f, .85f, wR, wG, wB);
    addBox(gl, d, rxC + half, 3.0f, zC, .4f, .5f, armL, .82f, .15f, .10f);
    addBox(gl, d, rxC - half, 3.0f, zC, .4f, .5f, armL, .82f, .15f, .10f);

    for (float bz = z0 + 3; bz < z1 - 1; bz += 6) {
      boolean red = ((int) ((bz - z0) / 6) % 2 == 0);
      float br = red ? .82f : .95f, bg = red ? .15f : .95f, bb = red ? .10f : .95f;
      addCyl(gl, d, lxC - half - .2f, 1.1f, bz, .22f, 1.2f, 8, br, bg, bb);
      addCyl(gl, d, lxC + half + .2f, 1.1f, bz, .22f, 1.2f, 8, br, bg, bb);
      addCyl(gl, d, rxC - half - .2f, 1.1f, bz, .22f, 1.2f, 8, br, bg, bb);
      addCyl(gl, d, rxC + half + .2f, 1.1f, bz, .22f, 1.2f, 8, br, bg, bb);
    }
    for (int k = 0; k <= 3; k++) {
      float bx = lxC - half + k * (armW / 3f);
      boolean red = k % 2 == 0;
      float br = red ? .82f : .95f, bg = red ? .15f : .95f, bb = red ? .10f : .95f;
      addCyl(gl, d, bx, 1.1f, z1 + .3f, .22f, 1.2f, 8, br, bg, bb);
      bx = rxC - half + k * (armW / 3f);
      addCyl(gl, d, bx, 1.1f, z1 + .3f, .22f, 1.2f, 8, br, bg, bb);
    }

    addBox(gl, d, lxC, .64f, zC, .38f, .02f, armL - 3, .90f, .85f, .15f);
    addBox(gl, d, rxC, .64f, zC, .38f, .02f, armL - 3, .90f, .85f, .15f);

    for (float bz = z0 + 8; bz < z1 - 5; bz += 14) {
      addBox(gl, d, lxC, .64f, bz, armW - 3, .02f, 5.5f, .90f, .90f, .88f);
      addBox(gl, d, rxC, .64f, bz, armW - 3, .02f, 5.5f, .90f, .90f, .88f);
    }

    for (float bz = z0 + 14; bz < z1 - 5; bz += 24) {
      addCyl(gl, d, lxC - half + 1.8f, 5.2f, bz, .13f, 6.5f, 6, .32f, .34f, .36f);
      addBox(gl, d, lxC - half + 3.3f, 8.5f, bz, 3.5f, .3f, .3f, .32f, .34f, .36f);
      addSphere(gl, d, lxC - half + 5.0f, 8.5f, bz, .55f, 6, 8, .95f, .95f, .80f);
      addCyl(gl, d, rxC + half - 1.8f, 5.2f, bz, .13f, 6.5f, 6, .32f, .34f, .36f);
      addBox(gl, d, rxC + half - 3.3f, 8.5f, bz, 3.5f, .3f, .3f, .32f, .34f, .36f);
      addSphere(gl, d, rxC + half - 5.0f, 8.5f, bz, .55f, 6, 8, .95f, .95f, .80f);
    }
  }

  private static void buildArbol(GL2 gl, VeracruzDemo d, float x, float z, float h) {
    addCyl(gl, d, x, h * .4f, z, .28f, h * .8f, 7, .52f, .38f, .22f);
    addCyl(gl, d, x, h * .85f, z, .22f, h * .3f, 7, .48f, .34f, .18f);
    addSphere(gl, d, x, h + 1.8f, z, 3.8f, 8, 12, .18f, .48f, .20f);
    addSphere(gl, d, x, h + 3.2f, z, 2.5f, 6, 10, .15f, .41f, .17f);
  }

  private static void buildCerezo(GL2 gl, VeracruzDemo d, float x, float z, float h) {
    addCyl(gl, d, x, h * .4f, z, .25f, h * .8f, 7, .48f, .36f, .25f);
    addSphere(gl, d, x, h + 1.2f, z, 3.5f, 12, 16, .98f, .65f, .75f);
    addSphere(gl, d, x - 1.5f, h + 2.2f, z - 1.5f, 2.5f, 10, 14, .95f, .55f, .70f);
    addSphere(gl, d, x + 1.5f, h + 2.2f, z + 1.5f, 2.5f, 10, 14, .95f, .55f, .70f);
  }

  private static void buildRemolcadores(GL2 gl, VeracruzDemo d) {
    buildBarco(gl, d, -52, 82, 12);
    buildBarco(gl, d, -14, 96, 5);
    buildBarco(gl, d, 26, 80, -6);
  }

  private static void buildBarco(GL2 gl, VeracruzDemo d, float x, float z, float rot) {
    Matrix4f base = new Matrix4f().translate(x, -0.6f, z).rotateY((float) Math.toRadians(rot)).scale(2.0f);
    d.add(box(gl, d, 14, 4.5f, 6, .78f, .18f, .12f), new Matrix4f(base).translate(0, 2.25f, 0));
    d.add(box(gl, d, 3, 4, 5, .70f, .16f, .11f), new Matrix4f(base).translate(8, 2, 0));
    d.add(box(gl, d, 12, .4f, 5.5f, .92f, .92f, .92f), new Matrix4f(base).translate(0, 4.7f, 0));
    d.add(box(gl, d, 5.5f, 4.5f, 4.5f, .85f, .85f, .82f), new Matrix4f(base).translate(1.5f, 7.2f, 0));
    d.add(box(gl, d, 5.2f, 1.2f, .35f, .35f, .52f, .80f), new Matrix4f(base).translate(1.5f, 8, 0 - 2.3f));
    d.add(cyl(gl, d, .8f, 4, 8, .14f, .14f, .14f), new Matrix4f(base).translate(0, 9.5f, .5f));
    d.add(cyl(gl, d, .85f, .5f, 8, .92f, .92f, .92f), new Matrix4f(base).translate(0, 11.5f, .5f));
  }

  private static void buildGrua(GL2 gl, VeracruzDemo d) {
    float gx = 155, gz = 35;
    for (float ox : new float[] { -10, 10 }) {
      for (float oz : new float[] { -5, 5 }) {
        addBox(gl, d, gx + ox, 17.5f, gz + oz, 1.5f, 35, 1.5f, .90f, .75f, .10f);
      }
    }
    addBox(gl, d, gx, 35, gz, 30, 2, 12, .90f, .75f, .10f);
    addBox(gl, d, gx - 10, 36.5f, gz, 50, 1.5f, 1.5f, .81f, .68f, .09f);
    addBox(gl, d, 146 + 22, 37, gz, 5, 5, 5, .30f, .32f, .34f); // gris
    addBox(gl, d, gx, 37, gz, 4, 3, 4, .77f, .64f, .09f);
  }

  // Horizonte
  private static void buildFondo(GL2 gl, VeracruzDemo d) {
    float[][] f = { { -220, -120, 20, 14 }, { -150, -115, 28, 18 }, { -80, -118, 22, 16 },
        { 100, -116, 25, 17 } };
  }

  private static void addCalleBox(GL2 gl, VeracruzDemo d, float x, float y, float z, float w, float h, float dp) {
    d.addStreet(box(gl, d, w, h, dp, 1f, 1f, 1f), new Matrix4f().translate(x, y, z));
  }

  private static void addCalleHorizontal(GL2 gl, VeracruzDemo d, float x, float z, float w, float dp) {
    addCalleBox(gl, d, x, 0.05f, z, w, 0.08f, dp);
  }

  private static void addCalleVertical(GL2 gl, VeracruzDemo d, float x, float z, float h_len, float w) {
    addCalleBox(gl, d, x, 0.05f, z, w, 0.08f, h_len);
  }

  private static void addBoxR(GL2 gl, VeracruzDemo d, float tx, float tz, float rx, float ry, float rz, float w,
      float h, float dp, float r, float g, float b, float angleRad) {
    Matrix4f mat = new Matrix4f().translate(tx, 0, tz).rotateY(angleRad).translate(rx - tx, ry, rz - tz);
    d.add(box(gl, d, w, h, dp, r, g, b), mat);
  }

  private static void addSphereR(GL2 gl, VeracruzDemo d, float tx, float tz, float rx, float ry, float rz, float rad,
      int sl, int st, float r, float g, float b, float angleRad) {
    Matrix4f mat = new Matrix4f().translate(tx, 0, tz).rotateY(angleRad).translate(rx - tx, ry, rz - tz);
    d.add(sphere(gl, d, rad, sl, st, r, g, b), mat);
  }

  private static void addCylR(GL2 gl, VeracruzDemo d, float tx, float tz, float rx, float ry, float rz, float rad,
      float h, int seg, float r, float g, float b, float angleRad) {
    Matrix4f mat = new Matrix4f().translate(tx, 0, tz).rotateY(angleRad).translate(rx - tx, ry, rz - tz);
    d.add(cyl(gl, d, rad, h, seg, r, g, b), mat);
  }

  private static void buildColonial(GL2 gl, VeracruzDemo d, float ex, float ez, float ew, float ed, int pisos,
      float angleDeg) {
    float angleRad = (float) Math.toRadians(angleDeg);
    float ph = pisos * 3.8f;
    float r = (pisos % 3 == 0) ? .72f : .93f, g = (pisos % 3 == 0) ? .38f : .91f,
        b = (pisos % 3 == 0) ? .25f : .87f;

    addBoxR(gl, d, ex, ez, ex, ph / 2, ez, ew, ph, ed, r, g, b, angleRad);
    addBoxR(gl, d, ex, ez, ex, ph + .4f, ez, ew + .6f, .8f, ed + .6f, .75f, .42f, .25f, angleRad);

    // Ventanas
    int cols = Math.max(2, (int) (ew / 4));
    for (int p = 0; p < pisos; p++) {
      float flH = ph / pisos, vy = p * flH + flH * .5f;
      for (int c = 0; c < cols; c++) {
        float vx = ex - ew / 2 + ew / (cols + 1f) * (c + 1);
        addBoxR(gl, d, ex, ez, vx, vy, ez - ed / 2 - .18f, 1.6f, 2.2f, .35f, .25f, .40f, .62f, angleRad);
      }
    }
  }

  private static void buildObjModel(GL2 gl, VeracruzDemo d) {
    try {
      int[] mesh = ObjLoader.loadObj(gl, d, "/modelos/Marlow66.obj");
      d.add(mesh,
          new Matrix4f().translate(170f, 2f, 120f).scale(2.0f).rotate((float) Math.toRadians(-135), 0f, 1f, 0f));

      int[] mesh2 = ObjLoader.loadObj(gl, d, "/modelos/10621_CoastGuardHelicopter.obj");
      // Asignar el helicóptero el movimiento de las teclas AWSDQE
      d.playerObj = new VeracruzDemo.SceneObj(mesh2, new Matrix4f(), 0f);
      d.scene.add(d.playerObj);

    } catch (Exception e) {
      System.err.println("Error al cargar los modelos OBJ: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
