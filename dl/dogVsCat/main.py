from keras import backend as K
import tensorflow as tf

from util import freeze_session, load_model

if __name__ == '__main__':
    model = load_model("model/model.h5")
    # frozen_graph = freeze_session(K.get_session(), output_names=[out.op.name for out in model.outputs])
    # tf.io.write_graph(frozen_graph, "model", "model.pb", as_text=False)

    model.save("model/keras/weights.h5")
    model_json = model.to_json()
    with open("model/keras/modelConfig.json", "w") as json_file:
        json_file.write(model_json)
