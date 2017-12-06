(function($) {
    var sendAction = function(enable) {
        $.post("${actionUrl}?action=" + (enable ? 'enable' : 'disable'),
                function() {
                    BS.reload(true);
                });
        return false;
    };
    $("#enable-btn").click(function() {
        return sendAction(true);
    });
    $("#disable-btn")
        .click(
            function() {
                if (!confirm("CloudShell Sandbox plugin will be suspended until enabled. Disable the plugin?"))
                    return false;
                return sendAction(false);
            });
})(jQuery);

var SandboxAdmin = {}

SandboxAdmin.SettingsForm = OO.extend(BS.AbstractPasswordForm, {
    setupEventHandlers: function() {
        var that = this;
        $('testConnection').on('click', this.testConnection.bindAsEventListener(this));

        this.setUpdateStateHandlers({
            updateState: function() {
                that.storeInSession();
            },
            saveState: function() {
                that.submitSettings();
            }
        });
    },

    storeInSession: function() {
        $("submitSettings").value = 'storeInSession';
        BS.PasswordFormSaver.save(this, this.formElement().action, BS.StoreInSessionListener);
    },

    submitSettings: function() {
        $("submitSettings").value = 'store';
        this.removeUpdateStateHandlers();
        BS.PasswordFormSaver.save(this, this.formElement().action,
            OO.extend(BS.ErrorsAwareListener, this.createErrorListener()));
        return false;
    },

    createErrorListener: function() {
        var that = this;
        return {
            onEmptyServerAddressError: function(elem) {
                $("errorServerAddress").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("serverAddress"));
            },
            onEmptyUsernameError: function(elem) {
                $("errorUsername").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("username"));
            },
            onEmptyPasswordError: function(elem) {
                $("errorPassword").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("password"));
            },
            onEmptyDomainError: function(elem) {
                $("errorDomain").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("domain"));
            },
            onCompleteSave: function(form, responseXML, err) {
                BS.ErrorsAwareListener.onCompleteSave(form, responseXML, err);
                if (!err) {
                    BS.XMLResponse.processRedirect(responseXML);
                } else {
                    that.setupEventHandlers();
                }
            }
        }
    },

    testConnection: function () {
        $("submitSettings").value = 'testConnection';
        var listener = OO.extend(BS.ErrorsAwareListener, this.createErrorListener());
        var oldOnCompleteSave = listener['onCompleteSave'];
        listener.onCompleteSave = function (form, responseXML, err) {
            oldOnCompleteSave(form, responseXML, err);
            if (!err) {
                form.enable();
                if (responseXML) {
                    var res = responseXML.getElementsByTagName("testConnectionResult")[0].
                        firstChild.nodeValue;
                    var success = res.includes("Test completed successfully")
                    BS.TestConnectionDialog.show(success, res, $('testConnection'));
                }
            }
        };
        BS.PasswordFormSaver.save(this, this.formElement().action, listener);
    }
});
