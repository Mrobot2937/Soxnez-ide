import io
import traceback
import contextlib


def run(code: str) -> str:
    """
    Executa o código Python recebido como string e devolve tudo que foi
    impresso (stdout), ou o traceback formatado caso dê erro.
    """
    buffer = io.StringIO()
    try:
        with contextlib.redirect_stdout(buffer):
            exec(code, {"__name__": "__main__"})
    except Exception:
        buffer.write("\n--- ERRO ---\n")
        buffer.write(traceback.format_exc())

    output = buffer.getvalue()
    return output if output else "(programa executou sem imprimir nada)"
