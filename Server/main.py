from flask import *
from flask_socketio import *
from flask_login import current_user, LoginManager
from flask_httpauth import HTTPBasicAuth
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
auth = HTTPBasicAuth()
app.config['SECRET_KEY'] = 'some super secret key!'
socketio = SocketIO(app, logger=True)

socketio.init_app(app, cors_allowed_origins="*")

login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'

users = {
    "rajeevan": generate_password_hash("865865")
}


@auth.verify_password
def verify_password(username, password):
    password_hash = users.get(username)

    if password_hash is None:
        print('User :', username, ' not available!')
        return False
    if not check_password_hash(password_hash, password):
        print('Invalid password provided for user : ', username)
        return False
    else:
        return username


@app.route('/')
def root():
    return "Hello world!"


@app.route('/html/<username>')
@auth.login_required
def html(username):
    return render_template('index.html', username=username)


@socketio.on('connect')
@auth.login_required
def connect_handler():
    user = auth.current_user()
    if user is None:
        print("User not found!")
        return False  # not allowed here
    else:
        print('User: ', user)
        emit('my response',
             {'message': '{0} has joined'.format(user)},
             broadcast=True)


# Receive a message from the front end HTML
@socketio.on('send_message')
@auth.login_required
def message_received(data):
    print('User: ', auth.username())
    print(data)
    emit('message_from_server', {'text': 'Message received!'})


# Actually Start the App
if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8000, debug=True, keyfile='key.pem', certfile='cert.pem')
