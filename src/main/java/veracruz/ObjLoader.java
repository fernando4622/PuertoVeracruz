/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package veracruz;

import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import org.joml.Vector2f;
/**
 *
 * @author ferna
 */
public class ObjLoader {

    public static int[] loadObj(GL2 gl, VeracruzDemo d, String resourcePath) throws Exception {
        InputStream is = ObjLoader.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("Recurso no encontrado: " + resourcePath);
        }
        String basePath = resourcePath.substring(0, resourcePath.lastIndexOf('/') + 1);
        return loadObjFromStream(gl, d, is, basePath);
    }

    public static int[] loadObjFromFile(GL2 gl, VeracruzDemo d, String filePath) throws Exception {
        String basePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
        return loadObjFromStream(gl, d, new FileInputStream(filePath), basePath);
    }

    private static int[] loadObjFromStream(GL2 gl, VeracruzDemo d, InputStream is, String basePath) throws Exception {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>(); // Coordenadas de textura (vt)
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector3f> faceColors = new ArrayList<>(); // Almacena el color asignado a cada vértice de cada triángulo

        // Diccionario para mapear el nombre del material con su color Vector3f (R, G, B)
        Map<String, Vector3f> materials = new HashMap<>();
        Map<String, BufferedImage> materialTextures = new HashMap<>(); // Almacena las texturas en memoria

        Vector3f currentBackgroundColor = new Vector3f(0.88f, 0.88f, 0.85f); // Gris por defecto
        String currentMaterialName = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length == 0) continue;

                switch (parts[0]) {
                    case "mtllib": // Detecta archivo de materiales
                        if (parts.length >= 2) {
                            loadMtl(basePath + parts[1], materials, materialTextures);
                        }
                        break;

                    case "usemtl": // Cambia el material activo para las siguientes caras
                        if (parts.length >= 2) {
                            currentMaterialName = parts[1];
                            if (materials.containsKey(currentMaterialName)) {
                                currentBackgroundColor = materials.get(currentMaterialName);
                            }
                        }
                        break;

                    case "v": // Vértice
                        if (parts.length >= 4) {
                            vertices.add(new Vector3f(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                            ));
                        }
                        break;

                    case "vt": // Coordenada de textura
                        if (parts.length >= 3) {
                            texCoords.add(new Vector2f(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2])
                            ));
                        }
                        break;

                    case "vn": // Normal
                        if (parts.length >= 4) {
                            normals.add(new Vector3f(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                            ));
                        }
                        break;

                    case "f": // Cara
                        if (parts.length >= 4) {
                            int[] faceVertices = new int[parts.length - 1];
                            int[] faceTexCoords = new int[parts.length - 1];
                            
                            for (int i = 1; i < parts.length; i++) {
                                String[] indices_parts = parts[i].split("/");
                                faceVertices[i - 1] = Integer.parseInt(indices_parts[0]) - 1;
                                
                                // Parsear el índice vt si existe
                                if (indices_parts.length > 1 && !indices_parts[1].isEmpty()) {
                                    faceTexCoords[i - 1] = Integer.parseInt(indices_parts[1]) - 1;
                                } else {
                                    faceTexCoords[i - 1] = -1;
                                }
                            }

                            for (int i = 1; i < faceVertices.length - 1; i++) {
                                indices.add(faceVertices[0]);
                                indices.add(faceVertices[i]);
                                indices.add(faceVertices[i + 1]);
                                
                                faceColors.add(getVertexColor(faceTexCoords[0], texCoords, materialTextures, currentMaterialName, currentBackgroundColor));
                                faceColors.add(getVertexColor(faceTexCoords[i], texCoords, materialTextures, currentMaterialName, currentBackgroundColor));
                                faceColors.add(getVertexColor(faceTexCoords[i + 1], texCoords, materialTextures, currentMaterialName, currentBackgroundColor));
                            }
                        }
                        break;
                }
            }
        }

        if (normals.isEmpty()) {
            normals = calculateNormals(vertices, indices);
        }

        // Construir array de vértices
        float[] vertexData = new float[indices.size() * 9];

        for (int i = 0; i < indices.size(); i++) {
            int vertIdx = indices.get(i);
            Vector3f pos = vertices.get(vertIdx);
            Vector3f norm = normals.get(Math.min(vertIdx, normals.size() - 1));
            Vector3f color = faceColors.get(i); // Color horneado o del material

            // Posición
            vertexData[i * 9 + 0] = pos.x;
            vertexData[i * 9 + 1] = pos.y;
            vertexData[i * 9 + 2] = pos.z;

            // Normal
            vertexData[i * 9 + 3] = norm.x;
            vertexData[i * 9 + 4] = norm.y;
            vertexData[i * 9 + 5] = norm.z;

            // Color
            vertexData[i * 9 + 6] = color.x;
            vertexData[i * 9 + 7] = color.y;
            vertexData[i * 9 + 8] = color.z;
        }

        int[] indexData = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexData[i] = i;
        }

        return d.upload(gl, vertexData, indexData);
    }

    private static Vector3f getVertexColor(int vtIndex, List<Vector2f> texCoords, Map<String, BufferedImage> textures, String materialName, Vector3f fallbackColor) {
        if (vtIndex >= 0 && vtIndex < texCoords.size() && materialName != null && textures.containsKey(materialName)) {
            BufferedImage img = textures.get(materialName);
            if (img != null) {
                Vector2f uv = texCoords.get(vtIndex);
                float u = uv.x;
                float v = uv.y;
                
                u = u - (float)Math.floor(u);
                v = v - (float)Math.floor(v);
                
                int x = (int) (u * (img.getWidth() - 1));
                int y = (int) ((1.0f - v) * (img.getHeight() - 1));
                
                x = Math.max(0, Math.min(x, img.getWidth() - 1));
                y = Math.max(0, Math.min(y, img.getHeight() - 1));
                
                int rgb = img.getRGB(x, y);
                float r = ((rgb >> 16) & 0xFF) / 255.0f;
                float g = ((rgb >> 8) & 0xFF) / 255.0f;
                float b = (rgb & 0xFF) / 255.0f;
                
                return new Vector3f(r, g, b);
            }
        }
        return fallbackColor;
    }

    private static void loadMtl(String mtlPath, Map<String, Vector3f> materials, Map<String, BufferedImage> materialTextures) {
        try (InputStream is = ObjLoader.class.getResourceAsStream(mtlPath)) {
            if (is == null) {
                System.out.println("No se pudo cargar el archivo MTL en: " + mtlPath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                String currentMaterial = null;
                String basePath = mtlPath.substring(0, mtlPath.lastIndexOf('/') + 1);
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] parts = line.split("\\s+");
                    if (parts.length == 0) continue;

                    if (parts[0].equals("newmtl")) {
                        currentMaterial = parts[1];
                    } else if (parts[0].equals("Kd") && currentMaterial != null) {
                        float r = Float.parseFloat(parts[1]);
                        float g = Float.parseFloat(parts[2]);
                        float b = Float.parseFloat(parts[3]);
                        materials.put(currentMaterial, new Vector3f(r, g, b));
                    } else if (parts[0].equals("map_Kd") && currentMaterial != null && parts.length >= 2) {
                        // Cargar la imagen en memoria con el BufferedImage
                        String texturePath = basePath + parts[1];
                        try {
                            InputStream imgStream = ObjLoader.class.getResourceAsStream(texturePath);
                            if (imgStream != null) {
                                BufferedImage img = ImageIO.read(imgStream);
                                if (img != null) {
                                    materialTextures.put(currentMaterial, img);
                                    System.out.println("Textura cargada correctamente en: " + parts[1]);
                                }
                            } else {
                                System.out.println("Imagen no encontrada en recursos: " + texturePath);
                            }
                        } catch (Exception e) {
                            System.out.println("No se pudo cargar textura: " + parts[1] + " - " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando archivo .mtl: " + e.getMessage());
        }
    }

    private static List<Vector3f> calculateNormals(List<Vector3f> vertices, List<Integer> indices) {
        List<Vector3f> normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            normals.add(new Vector3f(0, 0, 0));
        }

        for (int i = 0; i < indices.size(); i += 3) {
            int i0 = indices.get(i);
            int i1 = indices.get(i + 1);
            int i2 = indices.get(i + 2);

            Vector3f v0 = vertices.get(i0);
            Vector3f v1 = vertices.get(i1);
            Vector3f v2 = vertices.get(i2);

            Vector3f e1 = new Vector3f(v1).sub(v0);
            Vector3f e2 = new Vector3f(v2).sub(v0);
            Vector3f normal = new Vector3f(e1).cross(e2).normalize();

            normals.get(i0).add(normal);
            normals.get(i1).add(normal);
            normals.get(i2).add(normal);
        }

        for (Vector3f n : normals) {
            n.normalize();
        }

        return normals;
    }
}