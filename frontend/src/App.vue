<template>
	<div id="app">
		<nav-bar @logout="handleLogout" :user="user" />
		<div class="container-fluid">
			<router-view @login="handleLogin" />
		</div>
	</div>
</template>

<script>
import NavBar from "@/components/NavBar";
import auth from "./auth";

export default {
	components: {
		NavBar
	},
	data() {
		return {
			API_KEY: "AIzaSyABC5flhXSPvxkw4u_jWhH4Rq5wliY4A3k",
			GOOD_READS_API: "zAPW4dVltiCzgZbvtgnWWw",
			user: null
		};
	},
	methods: {
		handleLogout() {
			auth.logout();
			this.user = auth.getUser();
		},
		handleLogin() {
			this.user = auth.getUser();
		}
	},
	created() {
		this.user = auth.getUser();
	}
};
</script>

<style>
.container-fluid {
	padding: 0px !important;
	/* border: solid blue 1px; */
}
</style>
