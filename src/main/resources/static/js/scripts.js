$( document ).ready(function() {
$( "a.disconnect" ).on( "click", function( event ) {
    var $self = $(this);
    event.preventDefault();

    $( "#dialog-confirm" ).attr("title", "Disconnect from registry");
    $( "#dialog-confirm p").text("Do you really want to disconnect from registry?");

    $( "#dialog-confirm" ).dialog({
        resizable: false,
        height: "auto",
        width: 400,
        modal: true,
        position: { my: "center top", at: "center top+200", of: window },
        buttons: {
            "Disconnect": function() {
            window.location.href = $self.attr('href');
        },
            Cancel: function() {

            event.preventDefault();
            $( this ).dialog( "close" );
            }
      }
    });
} );


$( ".delete_tag" ).on( "click", function( event ) {
    var $self = $(this);
    event.preventDefault();

    var $tag = $(this).parent().prev().prev().text();

    $( "#dialog-confirm" ).attr("title", "Delete tag");
    $( "#dialog-confirm p").text("Do you really want to delete tag \"" + $tag + "\"?");

    $( "#dialog-confirm" ).dialog({
        resizable: false,
        height: "auto",
        width: 400,
        modal: true,
        my: "top",
        at: "top",
        of: window,
        buttons: {
            "Delete all items": function() {
            $( this ).dialog( "close" );
        },
            Cancel: function() {
            $( this ).dialog( "close" );
            }
      }
    });
} );
});