'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) { // event được tự động truyền vào hàm vì hàm connect đã được đăng ký bởi addEventListener
    username = document.querySelector('#name').value.trim();
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        // Tạo kết nối WebSocket
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError); // {} là các header tùy chọn, ở đây ko có header nào đc gửi
    }
    event.preventDefault(); // Ngăn chặn hành vi mặc định của sự kiện (ví dụ như nộp form)
}

function onConnected() {
    // Đăng ký vào /topic/public để nhận tin nhắn
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Thông báo cho server biết username mới
    stompClient.send('/app/chat.addUser',
        {},
        JSON.stringify({sender: username, type:'JOIN'})
    );
    connectingElement.classList.add('hidden');
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server ! Please try again !';
    connectingElement.style.color = 'red';
}

function onMessageReceived(payload) { // tham số payload được tự động truyền vào hàm bởi thư viện STOMP khi một tin nhắn được nhận từ máy chủ qua WebSocket
    var message = JSON.parse(payload.body); // payload.body chứa nội dung tin nhắn dưới dạng chuỗi JSON. Parse chuyển đổi nó thành 1 đối tượng JS

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' has joined the chat !';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' has left the chat !';
    } else {
        messageElement.classList.add('chat-message');

        // Tạo avatar user
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]); // Lấy chữ cái đầu tiên của tên để làm avatar
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        // Tạo tên user
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    // Hiển thị message
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight; // Cuộn khu vực hiển thị tin nhắn xuống cuối cùng để đảm bảo tin nhắn mới nhất luôn hiển thị.
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send(
            '/app/chat.sendMessage',
            {},
            JSON.stringify(chatMessage)
        );
        messageInput.value = '';
    }
    event.preventDefault();
}

// Tạo màu cho avatar
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true); // Khi có submit vào usernameForm, hàm connect sẽ được gọi, tham số 'true' để sự kiện sẽ được lắng nghe ở giai đoạn "capturing" (từ ngoài vào trong)
messageForm.addEventListener('submit', sendMessage, true);
