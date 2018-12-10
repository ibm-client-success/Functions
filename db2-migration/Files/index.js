function db2migrate(args) {

    var SSHClient = require('ssh2').Client;
    var response;

    const { source_destination, source_dash, source_port, source_pnj, source_BLUDB, source_BLUDBA, source_mqz, target_test, target_user, target_password, target_olive } = args;

    const params = {};
    params.source_destination = source_destination;
    params.source_dash = source_dash;
    params.source_port = source_port;
    params.source_pnj = source_pnj;
    params.source_BLUDB = source_BLUDB;
    params.source_BLUDBA = source_BLUDBA;
    params.source_mqz = source_mqz;
    params.target_test = target_test;
    params.target_user = target_user;
    params.target_password = target_password;
    params.target_olive = target_olive;

    const cmd = 'bash -x /home/db2inst1/bash.sh ' + params.source_destination + ' ' + params.source_dash + ' ' + params.source_port + ' ' +
        '' + params.source_pnj + ' ' + params.source_BLUDB + ' ' + params.source_BLUDBA + ' ' + params.source_mqz + ' ' +  params.target_test + ' ' +
        '' + params.target_user + ' ' + params.target_password + ' ' + params.target_olive

    const connectionOptions = {
        host: '<ENTER YOUR HOST (VM) IP HERE', // the ' is needed when filling this section.
        username: '<ENTER YOUR VM's USERNAME HERE', // the ' is needed when filling this section.
        password: '<ENTER YOUR VM's PASSWORD HERE>' // the ' is needed when filling this section.
    };

    console.log(cmd);
    console.log(connectionOptions);

    var conn = new SSHClient();
    conn.on('ready', function() {
        console.log('Client :: ready');
        conn.exec(cmd, function(err, stream) {
            if (err) throw err;
            stream.on('close', function(code, signal) {
                console.log('Stream :: close :: code: ' + code + ', signal: ' + signal);
                conn.end();
            }).on('data', function(data) {
                response='STDOUT: ' + data;
            }).stderr.on('data', function(data) {
                response = 'STDERR: ' + data;
            });
        });
    }).connect(connectionOptions);
    return response;
}

exports.main = db2migrate;
