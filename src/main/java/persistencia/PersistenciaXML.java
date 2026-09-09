package persistencia;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class PersistenciaXML {

    private static final Path ARCHIVO = Path.of("data", "datos.xml");

    private PersistenciaXML() {
    }

    public static boolean existeArchivo() {
        return Files.exists(ARCHIVO);
    }

    public static DatosCargados cargar() throws Exception {
        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(ARCHIVO.toFile());
        documento.getDocumentElement().normalize();

        Element adminXml = (Element) documento.getElementsByTagName("administrador").item(0);
        Administrador administrador = new Administrador(
                adminXml.getAttribute("id"),
                adminXml.getAttribute("clave")
        );

        ListaFuncionarios funcionarios = new ListaFuncionarios();
        NodeList funcionariosXml = documento.getElementsByTagName("funcionario");
        for (int i = 0; i < funcionariosXml.getLength(); i++) {
            Element elemento = (Element) funcionariosXml.item(i);
            Funcionario funcionario = new Funcionario(
                    elemento.getAttribute("id"),
                    elemento.getAttribute("nombre"),
                    elemento.getAttribute("telefono")
            );
            funcionario.setClave(elemento.getAttribute("clave"));
            funcionarios.addRecurso(funcionario);
        }

        ListaCategorias categorias = new ListaCategorias();
        NodeList categoriasXml = documento.getElementsByTagName("categoria");
        for (int i = 0; i < categoriasXml.getLength(); i++) {
            Element elemento = (Element) categoriasXml.item(i);
            categorias.addCategoria(new CategoriaRecurso(
                    elemento.getAttribute("id"),
                    elemento.getAttribute("descripcion")
            ));
        }

        ListaRecursos recursos = new ListaRecursos();
        NodeList recursosXml = documento.getElementsByTagName("recurso");
        for (int i = 0; i < recursosXml.getLength(); i++) {
            Element elemento = (Element) recursosXml.item(i);
            CategoriaRecurso categoria = categorias.buscarPorId(elemento.getAttribute("categoriaId"));
            if (categoria != null) {
                recursos.addRecurso(new Recurso(
                        elemento.getAttribute("id"),
                        categoria,
                        elemento.getAttribute("descripcion")
                ));
            }
        }

        ListaReservas reservas = new ListaReservas();
        NodeList reservasXml = documento.getElementsByTagName("reserva");
        for (int i = 0; i < reservasXml.getLength(); i++) {
            Element elemento = (Element) reservasXml.item(i);
            Reserva reserva = new Reserva(
                    elemento.getAttribute("funcionarioId"),
                    elemento.getAttribute("id"),
                    elemento.getAttribute("descripcion"),
                    elemento.getAttribute("actividad"),
                    LocalTime.parse(elemento.getAttribute("horaInicio")),
                    LocalTime.parse(elemento.getAttribute("horaFin")),
                    LocalDate.parse(elemento.getAttribute("fecha"))
            );
            reserva.setEstado(Reserva.Estado.valueOf(elemento.getAttribute("estado")));

            List<Recurso> recursosAsignados = new ArrayList<>();
            NodeList hijos = elemento.getChildNodes();
            for (int j = 0; j < hijos.getLength(); j++) {
                Node hijo = hijos.item(j);
                if (hijo instanceof Element
                        && "recursoAsignado".equals(((Element) hijo).getTagName())) {
                    Element recursoXml = (Element) hijo;
                    Recurso recurso = recursos.buscarPorId(recursoXml.getAttribute("id"));
                    if (recurso != null) recursosAsignados.add(recurso);
                }
            }
            reserva.setRecursos(recursosAsignados);
            reservas.add(reserva);
        }

        return new DatosCargados(administrador, funcionarios, categorias, recursos, reservas);
    }

    public static void guardar(Administrador administrador,
                               ListaFuncionarios funcionarios,
                               ListaCategorias categorias,
                               ListaRecursos recursos,
                               ListaReservas reservas) throws Exception {
        Files.createDirectories(ARCHIVO.getParent());

        Document documento = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
        Element raiz = documento.createElement("sistemaReservas");
        documento.appendChild(raiz);

        Element adminXml = documento.createElement("administrador");
        adminXml.setAttribute("id", administrador.getId());
        adminXml.setAttribute("clave", administrador.getClave());
        raiz.appendChild(adminXml);

        Element funcionariosXml = documento.createElement("funcionarios");
        raiz.appendChild(funcionariosXml);
        for (Funcionario funcionario : funcionarios.listarTodos()) {
            Element elemento = documento.createElement("funcionario");
            elemento.setAttribute("id", funcionario.getId());
            elemento.setAttribute("clave", funcionario.getClave());
            elemento.setAttribute("nombre", funcionario.getNombre());
            elemento.setAttribute("telefono", funcionario.getTelefono());
            funcionariosXml.appendChild(elemento);
        }

        Element categoriasXml = documento.createElement("categorias");
        raiz.appendChild(categoriasXml);
        for (CategoriaRecurso categoria : categorias.listarTodas()) {
            Element elemento = documento.createElement("categoria");
            elemento.setAttribute("id", categoria.getId());
            elemento.setAttribute("descripcion", categoria.getDesc());
            categoriasXml.appendChild(elemento);
        }

        Element recursosXml = documento.createElement("recursos");
        raiz.appendChild(recursosXml);
        for (Recurso recurso : recursos.listarTodos()) {
            Element elemento = documento.createElement("recurso");
            elemento.setAttribute("id", recurso.getId());
            elemento.setAttribute("categoriaId", recurso.getCategoria().getId());
            elemento.setAttribute("descripcion", recurso.getDescripcion());
            recursosXml.appendChild(elemento);
        }

        Element reservasXml = documento.createElement("reservas");
        raiz.appendChild(reservasXml);
        for (Reserva reserva : reservas.listarTodas()) {
            Element elemento = documento.createElement("reserva");
            elemento.setAttribute("id", reserva.getIdReserva());
            elemento.setAttribute("funcionarioId", reserva.getIdFuncionario());
            elemento.setAttribute("descripcion", reserva.getDescripcion());
            elemento.setAttribute("actividad", reserva.getActividad());
            elemento.setAttribute("fecha", reserva.getFecha().toString());
            elemento.setAttribute("horaInicio", reserva.getHoraInicio().toString());
            elemento.setAttribute("horaFin", reserva.getHoraFin().toString());
            elemento.setAttribute("estado", reserva.getEstado().name());

            for (Recurso recurso : reserva.getRecursos()) {
                Element asignado = documento.createElement("recursoAsignado");
                asignado.setAttribute("id", recurso.getId());
                elemento.appendChild(asignado);
            }
            reservasXml.appendChild(elemento);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(documento), new StreamResult(ARCHIVO.toFile()));
    }

    public static class DatosCargados {
        private final Administrador administrador;
        private final ListaFuncionarios funcionarios;
        private final ListaCategorias categorias;
        private final ListaRecursos recursos;
        private final ListaReservas reservas;

        public DatosCargados(Administrador administrador,
                             ListaFuncionarios funcionarios,
                             ListaCategorias categorias,
                             ListaRecursos recursos,
                             ListaReservas reservas) {
            this.administrador = administrador;
            this.funcionarios = funcionarios;
            this.categorias = categorias;
            this.recursos = recursos;
            this.reservas = reservas;
        }

        public Administrador administrador() {
            return administrador;
        }

        public ListaFuncionarios funcionarios() {
            return funcionarios;
        }

        public ListaCategorias categorias() {
            return categorias;
        }

        public ListaRecursos recursos() {
            return recursos;
        }

        public ListaReservas reservas() {
            return reservas;
        }
    }
}
