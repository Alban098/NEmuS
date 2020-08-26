package openGL.filters;

import exceptions.InvalidFileException;
import openGL.shader.uniform.*;
import org.joml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import utils.Dialogs;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class Filter {

    private static final Filter defaultFilter = new Filter("Vertical Flip", "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl", "Flip the screen vertically");
    private static final List<Filter> allFilters = new ArrayList<>();

    private final String name;
    final String vertexFile;
    final String fragmentFile;
    private final String description;
    private final Map<String, Uniform> uniforms;

    private Filter(String name, String vertex, String fragment, String description, Uniform... uniforms) {
        this.name = name;
        this.vertexFile = vertex;
        this.fragmentFile = fragment;
        this.uniforms = new HashMap<>();
        this.description = description;
        for (Uniform uniform : uniforms)
            this.uniforms.put(uniform.getName(), uniform);
    }

    public Uniform getUniform(String name) {
        return uniforms.get(name);
    }

    public Collection<Uniform> getAllUniforms() {
        return uniforms.values();
    }

    public String getDescription() {
        return description;
    }

    public Parameter[] getDefaultParameters() {
        Parameter[] parameters = new Parameter[uniforms.size()];
        int i = 0;
        for (Uniform uniform : uniforms.values()) {
            ParameterType type = null;
            if (uniform instanceof UniformBoolean) type = ParameterType.BOOLEAN;
            else if (uniform instanceof UniformInteger) type = ParameterType.INTEGER;
            else if (uniform instanceof UniformFloat) type = ParameterType.FLOAT;
            else if (uniform instanceof UniformVec2) type = ParameterType.VEC2;
            else if (uniform instanceof UniformVec3) type = ParameterType.VEC3;
            else if (uniform instanceof UniformVec4) type = ParameterType.VEC4;
            else if (uniform instanceof UniformMat2) type = ParameterType.MAT2;
            else if (uniform instanceof UniformMat3) type = ParameterType.MAT3;
            else if (uniform instanceof UniformMat4) type = ParameterType.MAT4;
            parameters[i] = new Parameter(uniform.getName(), uniform.getDefault(), type);
            i++;
        }
        return parameters;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Filter> getAll() {
        return allFilters;
    }

    public static Filter getDefault() {
        return defaultFilter;
    }

    public static void init() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File("filters.xml");
            Document xml;
            xml = builder.parse(fileXML);
            Element filtersList = (Element) xml.getElementsByTagName("filters").item(0);
            if (filtersList == null)
                throw new InvalidFileException("filters.xml file corrupted (filters node not found)");
            NodeList filters = filtersList.getElementsByTagName("filter");

            for (int i = 0; i < filters.getLength(); i++) {
                Element filter = (Element) filters.item(i);
                Element name = (Element)filter.getElementsByTagName("name").item(0);
                Element vertex = (Element)filter.getElementsByTagName("vertex").item(0);
                Element fragment = (Element)filter.getElementsByTagName("fragment").item(0);
                Element desc = (Element)filter.getElementsByTagName("description").item(0);
                NodeList uniforms = ((Element)filter.getElementsByTagName("uniforms").item(0)).getElementsByTagName("uniform");
                Uniform[] tmp = new Uniform[uniforms.getLength()];
                for (int j = 0; j < uniforms.getLength(); j++) {
                    Element e = (Element) uniforms.item(j);
                    switch (e.getAttribute("type")) {
                        case "float" -> tmp[j] = new UniformFloat(e.getAttribute("name").replaceAll(" ", "_"), Float.parseFloat(e.getAttribute("default")));
                        case "bool" -> tmp[j] = new UniformBoolean(e.getAttribute("name").replaceAll(" ", "_"), Boolean.parseBoolean(e.getAttribute("default")));
                        case "int" -> tmp[j] = new UniformInteger(e.getAttribute("name").replaceAll(" ", "_"), Integer.parseInt(e.getAttribute("default")));
                        case "mat2" -> {
                            String[] defMat2 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defMat2.length != 4)
                                throw new Exception("Invalid default value size (mat2 must be 4 float)");
                            Matrix2f mat2 = new Matrix2f(Integer.parseInt(defMat2[0]), Integer.parseInt(defMat2[1]), Integer.parseInt(defMat2[2]), Integer.parseInt(defMat2[3]));
                            tmp[j] = new UniformMat2(e.getAttribute("name").replaceAll(" ", "_"), mat2);
                        }
                        case "mat3" -> {
                            String[] defMat3 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defMat3.length != 9)
                                throw new Exception("Invalid default value size (mat3 must be 9 float)");
                            Matrix3f mat3 = new Matrix3f(Integer.parseInt(defMat3[0]), Integer.parseInt(defMat3[1]), Integer.parseInt(defMat3[2]), Integer.parseInt(defMat3[3]), Integer.parseInt(defMat3[4]), Integer.parseInt(defMat3[5]), Integer.parseInt(defMat3[6]), Integer.parseInt(defMat3[7]), Integer.parseInt(defMat3[8]));
                            tmp[j] = new UniformMat3(e.getAttribute("name").replaceAll(" ", "_"), mat3);
                        }
                        case "mat4" -> {
                            String[] defMat4 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defMat4.length != 16)
                                throw new Exception("Invalid default value size (mat4 must be 16 float)");
                            Matrix4f mat4 = new Matrix4f(Integer.parseInt(defMat4[0]), Integer.parseInt(defMat4[1]), Integer.parseInt(defMat4[2]), Integer.parseInt(defMat4[3]), Integer.parseInt(defMat4[4]), Integer.parseInt(defMat4[5]), Integer.parseInt(defMat4[6]), Integer.parseInt(defMat4[7]), Integer.parseInt(defMat4[8]), Integer.parseInt(defMat4[9]), Integer.parseInt(defMat4[10]), Integer.parseInt(defMat4[11]), Integer.parseInt(defMat4[12]), Integer.parseInt(defMat4[13]), Integer.parseInt(defMat4[14]), Integer.parseInt(defMat4[15]));
                            tmp[j] = new UniformMat4(e.getAttribute("name").replaceAll(" ", "_"), mat4);
                        }
                        case "vec2" -> {
                            String[] defVec2 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defVec2.length != 2)
                                throw new Exception("Invalid default value size (vec2 must be 2 float)");
                            Vector2f vec2 = new Vector2f(Integer.parseInt(defVec2[0]), Integer.parseInt(defVec2[1]));
                            tmp[j] = new UniformVec2(e.getAttribute("name").replaceAll(" ", "_"), vec2);
                        }
                        case "vec3" -> {
                            String[] defVec3 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defVec3.length != 3)
                                throw new Exception("Invalid default value size (vec3 must be 3 float)");
                            Vector3f vec3 = new Vector3f(Integer.parseInt(defVec3[0]), Integer.parseInt(defVec3[1]), Integer.parseInt(defVec3[2]));
                            tmp[j] = new UniformVec3(e.getAttribute("name").replaceAll(" ", "_"), vec3);
                        }
                        case "vec4" -> {
                            String[] defVec4 = e.getAttribute("default").replaceAll(" ", "").split(";");
                            if (defVec4.length != 4)
                                throw new Exception("Invalid default value size (vec4 must be 4 float)");
                            Vector4f vec4 = new Vector4f(Integer.parseInt(defVec4[0]), Integer.parseInt(defVec4[1]), Integer.parseInt(defVec4[2]), Integer.parseInt(defVec4[3]));
                            tmp[j] = new UniformVec4(e.getAttribute("name").replaceAll(" ", "_"), vec4);
                        }
                        default -> throw new Exception("Invalid uniform type : " + e.getNodeName());
                    }
                }
                allFilters.add(new Filter(name.getTextContent(), vertex.getTextContent(), fragment.getTextContent(), desc.getTextContent(), tmp));
            }
        } catch (Exception e) {
            Dialogs.showException("Error Loading Filters", "An error has occurred during Filters loading", e);
        }
    }
}
