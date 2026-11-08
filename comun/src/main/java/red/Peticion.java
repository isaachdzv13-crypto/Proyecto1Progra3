package red;

import java.io.Serializable;

public class Peticion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String operacion;
    private final Object[] parametros;

    public Peticion(String operacion, Object... parametros) {
        this.operacion = operacion;
        this.parametros = parametros;
    }

    public String getOperacion() {
        return operacion;
    }

    public Object[] getParametros() {
        return parametros;
    }
}
